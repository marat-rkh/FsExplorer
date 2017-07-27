package fs.explorer.datasource;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class LocalFilesProvider implements TreeDataProvider {
    private final String topDirName = "/";

    @Override
    public void getTopNode(Consumer<TreeNodeData> onComplete) {
        FsPath fsPath = new FsPath(topDirName, topDirName, /*isDirectory*/true);
        onComplete.accept(new TreeNodeData(fsPath));
    }

    @Override
    public void getNodesFor(TreeNodeData node, Consumer<List<TreeNodeData>> onComplete) {
        onComplete.accept(Collections.emptyList());
    }
}
