package fs.explorer.datasource;

import java.util.function.Consumer;

public class LocalFilesProvider implements TreeDataProvider {
    private final String topDirName = "/";

    @Override
    public void getTopNode(Consumer<TreeNodeData> onComplete) {
        FsPath fsPath = new FsPath(topDirName, topDirName);
        onComplete.accept(new TreeNodeData(fsPath));
    }
}
