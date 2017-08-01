package fs.explorer.providers.dirtree;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FsDataProvider implements TreeDataProvider {
    private FsPath topDir;
    private FsManager fsManager;

    private static final String DATA_READ_ERROR = "data read error";

    public FsDataProvider(FsPath topDir, FsManager fsManager) {
        this.topDir = topDir;
        this.fsManager = fsManager;
    }

    @Override
    public void getTopNode(Consumer<TreeNodeData> onComplete) {
        onComplete.accept(new TreeNodeData(topDir.getLastComponent(), topDir));
    }

    @Override
    public void getNodesFor(
            TreeNodeData node,
            Consumer<List<TreeNodeData>> onComplete,
            Consumer<String> onFail
    ) {
        FsPath nodeFsPath = node.getFsPath();
        if(!nodeFsPath.isDirectory()) {
            onFail.accept("");
            return;
        }
        try {
            List<FsPath> entries = fsManager.list(nodeFsPath);
            Map<Boolean, List<TreeNodeData>> data = entries.stream()
                    .map(FsDataProvider::toTreeNodeData)
                    .collect(Collectors.partitioningBy(d -> d.getFsPath().isDirectory()));
            List<TreeNodeData> dirsData = data.get(true);
            List<TreeNodeData> filesData = data.get(false);
            dirsData.sort(Comparator.comparing(TreeNodeData::getLabel));
            filesData.sort(Comparator.comparing(TreeNodeData::getLabel));
            dirsData.addAll(filesData);
            onComplete.accept(dirsData);
        } catch (IOException e) {
            onFail.accept(DATA_READ_ERROR);
        }
    }

    private static TreeNodeData toTreeNodeData(FsPath fsPath) {
        String label = fsPath.getLastComponent();
        if(label.isEmpty()) {
            label = "?";
        }
        return new TreeNodeData(label, fsPath);
    }
}
