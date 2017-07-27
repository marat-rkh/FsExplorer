package fs.explorer.datasource;

import java.util.function.Consumer;

public interface TreeDataProvider {
    void getTopNode(Consumer<TreeNodeData> onComplete);
}
