package fs.explorer.controllers.preivew;

import fs.explorer.datasource.PreviewData;
import fs.explorer.datasource.TreeNodeData;
import fs.explorer.gui.dirtree.DirTreeSelectionListener;
import fs.explorer.gui.PreviewPane;

import javax.swing.*;

public class PreviewUpdater implements DirTreeSelectionListener {
    private final PreviewPane previewPane;

    public PreviewUpdater(PreviewPane previewPane) {
        this.previewPane = previewPane;
    }

    public void updatePreview(TreeNodeData nodeData) {
        // TODO load preview data
        PreviewData previewData = new PreviewData(
                nodeData.toString().getBytes(), PreviewData.Type.TEXT);
        JComponent preview = PreviewRenderer.render(previewData);
        previewPane.updatePreview(preview);
    }

    @Override
    public void valueChanged(TreeNodeData e) {
        updatePreview(e);
    }
}
