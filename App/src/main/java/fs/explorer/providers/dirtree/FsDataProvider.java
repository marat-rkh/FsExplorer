package fs.explorer.providers.dirtree;

import java.util.List;
import java.util.function.Consumer;

public interface FsDataProvider {
    void getTopNode(Consumer<TreeNodeData> onComplete);

    void getNodesFor(
            TreeNodeData node,
            Consumer<List<TreeNodeData>> onComplete,
            Consumer<String> onFail
    );
}
