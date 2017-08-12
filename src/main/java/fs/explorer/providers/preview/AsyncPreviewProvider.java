package fs.explorer.providers.preview;

import fs.explorer.providers.dirtree.TreeNodeData;
import fs.explorer.utils.Disposable;

import javax.swing.*;
import java.util.concurrent.*;

public class AsyncPreviewProvider implements PreviewProvider, Disposable {
    private final PreviewProvider previewProvider;
    private final ScheduledThreadPoolExecutor executor;
    private final long taskStartDelayMilliseconds;
    private ScheduledFuture<?> currentTask;

    private static final String INTERNAL_ERROR = "internal error";

    public AsyncPreviewProvider(PreviewProvider previewProvider, long taskStartDelayMilliseconds) {
        this.previewProvider = previewProvider;
        executor = new ScheduledThreadPoolExecutor(4);
        executor.setRemoveOnCancelPolicy(true);
        this.taskStartDelayMilliseconds = taskStartDelayMilliseconds;
    }

    @Override
    public void getPreview(
            TreeNodeData data,
            PreviewContext context,
            PreviewProgressHandler progressHandler
    ) {
        try {
            if(currentTask != null) {
                currentTask.cancel(true);
            }
            PreviewProgressHandler asyncHandler = new PreviewProgressHandler() {
                @Override
                public void onComplete(JComponent preview) {
                    SwingUtilities.invokeLater(() -> progressHandler.onComplete(preview));
                }

                @Override
                public void onError(String errorMessage) {
                    SwingUtilities.invokeLater(() -> progressHandler.onError(errorMessage));
                }

                @Override
                public void onCanNotRenderer() {
                    SwingUtilities.invokeLater(progressHandler::onCanNotRenderer);
                }
            };
            Runnable task = () -> previewProvider.getPreview(data, context, asyncHandler);
            currentTask = executor.schedule(
                    task, taskStartDelayMilliseconds, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            progressHandler.onError(INTERNAL_ERROR);
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
