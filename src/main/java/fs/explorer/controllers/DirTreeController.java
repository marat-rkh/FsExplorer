package fs.explorer.controllers;

import fs.explorer.datasource.TreeNodeData;
import fs.explorer.gui.dirtree.DirTreeSelectionListener;
import fs.explorer.model.preview.PreviewModel;

public class DirTreeController implements DirTreeSelectionListener {
    private final PreviewModel previewModel;

    public DirTreeController(PreviewModel previewModel) {
        this.previewModel = previewModel;
    }

    @Override
    public void valueChanged(TreeNodeData e) {
        previewModel.updatePreview(e);
    }
}
