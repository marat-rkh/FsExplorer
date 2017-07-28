package fs.explorer.controllers;

import fs.explorer.datasource.TreeNodeData;
import fs.explorer.gui.dirtree.DirTreeSelectionListener;
import fs.explorer.model.preview.PreviewModel;

public class PreviewUpdater implements DirTreeSelectionListener {
    private final PreviewModel previewModel;

    public PreviewUpdater(PreviewModel previewModel) {
        this.previewModel = previewModel;
    }

    @Override
    public void valueChanged(TreeNodeData e) {
        previewModel.updatePreview(e);
    }
}
