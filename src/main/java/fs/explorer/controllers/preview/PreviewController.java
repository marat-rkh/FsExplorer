package fs.explorer.controllers.preview;

import fs.explorer.providers.PreviewData;
import fs.explorer.providers.TreeNodeData;
import fs.explorer.views.PreviewPane;

import javax.swing.*;

public class PreviewController {
    private final PreviewPane previewPane;

    public PreviewController(PreviewPane previewPane) {
        this.previewPane = previewPane;
    }

    public void updatePreview(TreeNodeData nodeData) {
        // TODO load preview data
        PreviewData previewData = new PreviewData(
                nodeData.toString().getBytes(), PreviewData.Type.TEXT);
        JComponent preview = PreviewRenderer.render(previewData);
        previewPane.updatePreview(preview);
    }
}
