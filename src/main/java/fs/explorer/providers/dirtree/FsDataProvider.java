package fs.explorer.providers.dirtree;

import fs.explorer.providers.dirtree.path.FsPath;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class is thread safe if instances of FsManager
 * used with it (passed to constructors or methods) are thread safe.
 */
public class FsDataProvider implements TreeDataProvider {
    private final FsPath topDir;
    private final FsManager fsManager;

    private static final String INTERNAL_ERROR = "internal error";
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
        if(!node.getPath().isFsPath()) {
            onFail.accept(INTERNAL_ERROR);
            return;
        }
        FsPath nodeFsPath = node.getPath().asFsPath();
        if(!nodeFsPath.isDirectory()) {
            onFail.accept(INTERNAL_ERROR);
            return;
        }
        try {
            List<FsPath> entries = fsManager.list(nodeFsPath);
            // TODO too much streams
            Map<Boolean, List<FsPath>> data = entries.stream()
                    .collect(Collectors.partitioningBy(FsPath::isDirectory));
            List<TreeNodeData> dirsData = data.get(true).stream()
                    .map(FsDataProvider::toTreeNodeData).collect(Collectors.toList());
            List<TreeNodeData> filesData = data.get(false).stream()
                    .map(FsDataProvider::toTreeNodeData).collect(Collectors.toList());
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
