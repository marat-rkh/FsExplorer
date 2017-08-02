package fs.explorer.providers.preview;

import fs.explorer.providers.dirtree.TreeNodeData;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class AsyncPreviewProvider implements PreviewProvider {
    private final PreviewProvider previewProvider;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AsyncPreviewProvider(PreviewProvider previewProvider) {
        this.previewProvider = previewProvider;
    }

    @Override
    public void getTextPreview(
            TreeNodeData data,
            Consumer<JComponent> onComplete,
            Consumer<String> onFail
    ) {
        executor.execute(() ->
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
        executor.execute(() ->
            previewProvider.getImagePreview(
                data,
                arg -> SwingUtilities.invokeLater(() -> onComplete.accept(arg)),
                arg -> SwingUtilities.invokeLater(() -> onFail.accept(arg))
            )
        );
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
