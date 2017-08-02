package fs.explorer.providers.dirtree;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class AsyncFsDataProvider implements TreeDataProvider {
    private final FsDataProvider fsDataProvider;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private static final String INTERNAL_ERROR = "internal error";

    public AsyncFsDataProvider(FsDataProvider fsDataProvider) {
        this.fsDataProvider = fsDataProvider;
    }

    @Override
    public void getTopNode(Consumer<TreeNodeData> onComplete) {
        fsDataProvider.getTopNode(onComplete);
    }

    @Override
    public void getNodesFor(
            TreeNodeData node,
            Consumer<List<TreeNodeData>> onComplete,
            Consumer<String> onFail
    ) {
        try {
            executor.execute(() ->
                fsDataProvider.getNodesFor(
                    node,
                    arg -> SwingUtilities.invokeLater(() -> onComplete.accept(arg)),
                    arg -> SwingUtilities.invokeLater(() -> onFail.accept(arg))
                )
            );
        } catch (RejectedExecutionException e) {
            onFail.accept(INTERNAL_ERROR);
        }
    }

    // TODO add to resource cleanup
    public void shutdown() {
        executor.shutdown();
        try {
            if(!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if(!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    // failed to shutdown
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
