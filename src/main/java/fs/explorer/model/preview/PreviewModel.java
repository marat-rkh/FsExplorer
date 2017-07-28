package fs.explorer.model.preview;

import fs.explorer.datasource.TreeNodeData;
import fs.explorer.datasource.PreviewData;

import javax.swing.*;

public class PreviewModel {
    public JComponent buildPreview(TreeNodeData nodeData) {
        // TODO load preview data
        PreviewData previewData = new PreviewData(
                nodeData.toString().getBytes(), PreviewData.Type.TEXT);
        return PreviewRenderer.render(previewData);
    }
}
