package fs.explorer.listeners;

import fs.explorer.datasource.TreeNodeData;
import fs.explorer.gui.dirtree.DirTreeSelectionListener;
import fs.explorer.gui.PreviewPane;

public class PreviewUpdater implements DirTreeSelectionListener {
    private final PreviewPane previewPane;

    public PreviewUpdater(PreviewPane previewPane) {
        this.previewPane = previewPane;
    }

    @Override
    public void valueChanged(TreeNodeData e) {
        previewPane.updatePreview(e);
    }
}
