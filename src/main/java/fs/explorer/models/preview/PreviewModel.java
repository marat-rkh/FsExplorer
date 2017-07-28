package fs.explorer.models.preview;

import fs.explorer.datasource.PreviewData;
import fs.explorer.datasource.TreeNodeData;
import fs.explorer.views.PreviewPane;

import javax.swing.*;

public class PreviewModel {
    private final PreviewPane previewPane;

    public PreviewModel(PreviewPane previewPane) {
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
