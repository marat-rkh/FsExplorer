package fs.explorer.datasource;

import java.util.function.Consumer;

public class LocalFilesProvider implements TreeDataProvider {
    private final String topDirName = "/";

    @Override
    public void getTopNode(Consumer<TreeNodeData> onComplete) {
        onComplete.accept(new TreeNodeData(topDirName, TreeNodeData.Type.DIRECTORY));
    }
}
