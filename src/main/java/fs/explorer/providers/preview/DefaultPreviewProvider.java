package fs.explorer.providers.preview;

import fs.explorer.providers.FsManager;
import fs.explorer.providers.TreeNodeData;

import javax.swing.*;
import java.io.IOException;
import java.util.function.Consumer;

public class DefaultPreviewProvider implements PreviewProvider {
    private FsManager fsManager;
    private final PreviewRenderer previewRenderer;

    private static final String FILE_READ_FAILED = "failed to read file";
    private static final String RENDERING_FAILED = "failed to create preview";
    private static final String INTERNAL_ERROR = "internal error";

    public DefaultPreviewProvider(FsManager fsManager, PreviewRenderer previewRenderer) {
        this.fsManager = fsManager;
        this.previewRenderer = previewRenderer;
    }

    public void resetFsManager(FsManager fsManager) {
        this.fsManager = fsManager;
    }

    @Override
    public void getTextPreview(
            TreeNodeData data,
            Consumer<JComponent> onComplete,
            Consumer<String> onFail
    ) {
        if(data == null) {
            onFail.accept(INTERNAL_ERROR);
            return;
        }
        byte[] bytes = readContents(data);
        if(bytes == null) {
            onFail.accept(FILE_READ_FAILED);
            return;
        }
        // TODO support encodings
        String contents = new String(bytes);
        TextPreviewData previewData = new TextPreviewData(contents);
        JComponent preview = previewRenderer.renderText(previewData);
        if(preview != null) {
            onComplete.accept(preview);
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
        if(data == null) {
            onFail.accept(INTERNAL_ERROR);
            return;
        }
        byte[] bytes = readContents(data);
        if(bytes == null) {
            onFail.accept(FILE_READ_FAILED);
            return;
        }
        ImagePreviewData previewData = new ImagePreviewData(bytes);
        JComponent preview = previewRenderer.renderImage(previewData);
        if(preview != null) {
            onComplete.accept(preview);
        } else {
            onFail.accept(RENDERING_FAILED);
        }
    }

    private byte[] readContents(TreeNodeData data) {
        try {
            return fsManager.readFile(data.getFsPath());
        } catch (IOException e) {
            return null;
        }
    }
}
