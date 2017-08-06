package fs.explorer.providers.preview;

import fs.explorer.providers.dirtree.TreeNodeData;
import fs.explorer.utils.Disposable;

import javax.swing.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

// @NotThreadSafe
public class AsyncPreviewProvider implements PreviewProvider, Disposable {
    private final PreviewProvider previewProvider;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> currentTask;

    private static final String INTERNAL_ERROR = "internal error";

    public AsyncPreviewProvider(PreviewProvider previewProvider) {
        this.previewProvider = previewProvider;
    }

    @Override
    public void getTextPreview(
            TreeNodeData data,
            Consumer<JComponent> onComplete,
            Consumer<String> onFail
    ) {
        runAsync(onFail, () ->
            previewProvider.getTextPreview(
                data,
                arg -> SwingUtilities.invokeLater(() -> onComplete.accept(arg)),
                arg -> SwingUtilities.invokeLater(() -> onFail.accept(arg))
            )
        );
    }

    @Override
    public void getImagePreview(
            TreeNodeData data,
            Consumer<JComponent> onComplete,
            Consumer<String> onFail
    ) {
        runAsync(onFail, () ->
            previewProvider.getImagePreview(
                data,
                arg -> SwingUtilities.invokeLater(() -> onComplete.accept(arg)),
                arg -> SwingUtilities.invokeLater(() -> onFail.accept(arg))
            )
        );
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

    private void runAsync(Consumer<String> onFail, Runnable getPreviewTask) {
        try {
            if(currentTask != null) {
                currentTask.cancel(/*mayInterruptIfRunning*/true);
            }
            currentTask = executor.submit(getPreviewTask);
        } catch (RejectedExecutionException e) {
            onFail.accept(INTERNAL_ERROR);
        }
    }
}
