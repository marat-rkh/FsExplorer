package fs.explorer.providers.preview;

import fs.explorer.providers.FsManager;
import fs.explorer.providers.TreeNodeData;

import javax.swing.*;
import java.io.IOException;
import java.util.function.Consumer;

public class PreviewProvider {
    private final FsManager fsManager;

    private static final String FILE_READ_FAILED = "failed to read file";

    public PreviewProvider(FsManager fsManager) {
        this.fsManager = fsManager;
    }

    public void getTextPreview(
            TreeNodeData data,
            Consumer<JComponent> onComplete,
            Consumer<String> onFail
    ) {
        byte[] bytes = null;
        try {
            bytes = fsManager.readFile(data.getFsPath());
        } catch (IOException e) {
            onFail.accept(FILE_READ_FAILED);
            return;
        }
        // TODO support encodings
        String contents = new String(bytes);
        TextPreviewData previewData = new TextPreviewData(contents);
        JComponent preview = PreviewRenderer.renderText(previewData);
        onComplete.accept(preview);
    }

    public void getImagePreview(
            TreeNodeData data,
            Consumer<JComponent> onComplete,
            Consumer<String> onFail
    ) {
        byte[] bytes = null;
        try {
            bytes = fsManager.readFile(data.getFsPath());
        } catch (IOException e) {
            onFail.accept(FILE_READ_FAILED);
            return;
        }
        ImagePreviewData previewData = new ImagePreviewData(bytes);
        JComponent preview = PreviewRenderer.renderImage(previewData);
        onComplete.accept(preview);
    }
}
