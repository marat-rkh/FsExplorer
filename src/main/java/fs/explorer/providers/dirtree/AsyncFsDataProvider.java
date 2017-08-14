package fs.explorer.providers.dirtree;

import java.util.List;
import java.util.function.Consumer;

public interface AsyncFsDataProvider {
    void getTopNode(Consumer<TreeNodeData> onComplete);

    TreeNodeLoader getNodesFor(
            TreeNodeData node,
            Consumer<List<TreeNodeData>> onComplete,
            Consumer<String> onFail
    );
}
