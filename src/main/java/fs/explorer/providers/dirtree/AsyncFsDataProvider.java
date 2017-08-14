package fs.explorer.providers.dirtree;

import fs.explorer.providers.utils.loading.TreeNodeFutureLoader;
import fs.explorer.providers.utils.loading.TreeNodeLoader;
import fs.explorer.utils.Disposable;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class AsyncFsDataProvider implements AsyncTreeDataProvider, Disposable {
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
    public TreeNodeLoader getNodesFor(
            TreeNodeData node,
            Consumer<List<TreeNodeData>> onComplete,
            Consumer<String> onFail
    ) {
        try {
            Future<?> task = executor.submit(() ->
                fsDataProvider.getNodesFor(
                    node,
                    arg -> SwingUtilities.invokeLater(() -> onComplete.accept(arg)),
                    arg -> SwingUtilities.invokeLater(() -> onFail.accept(arg))
                )
            );
            return new TreeNodeFutureLoader(task);
        } catch (RejectedExecutionException e) {
            onFail.accept(INTERNAL_ERROR);
            return null;
        }
    }

    public void shutdownNow() {
        executor.shutdownNow();
        try {
            if(!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                // failed to shutdown
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void dispose() {
        shutdownNow();
    }
}
