package fs.explorer.providers.preview;

import fs.explorer.providers.FsManager;
import fs.explorer.providers.TreeNodeData;

import javax.swing.*;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

public class DefaultPreviewProvider implements PreviewProvider {
    private final FsManager fsManager;

    private static final String FILE_READ_FAILED = "failed to read file";
    private static final String RENDERING_FAILED = "failed to create preview";

    public DefaultPreviewProvider(FsManager fsManager) {
        this.fsManager = fsManager;
    }

    @Override
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
        Optional<JComponent> optPreview = PreviewRenderer.renderText(previewData);
        if(optPreview.isPresent()) {
            onComplete.accept(optPreview.get());
        } else {
            onFail.accept(RENDERING_FAILED);
        }
    }

    @Override
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
        Optional<JComponent> optPreview = PreviewRenderer.renderImage(previewData);
        if(optPreview.isPresent()) {
            onComplete.accept(optPreview.get());
        } else {
            onFail.accept(RENDERING_FAILED);
        }
    }
}
