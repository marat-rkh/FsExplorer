package fs.explorer.gui;

import fs.explorer.datasource.TreeNodeData;
import fs.explorer.model.preview.PreviewModel;

import javax.swing.*;

public class PreviewPane {
    private final JScrollPane scrollPane;
    private final PreviewModel previewModel;

    public PreviewPane() {
        this.scrollPane = new JScrollPane(new JLabel("Preview"));
        this.previewModel = new PreviewModel();
    }

    public void updatePreview(TreeNodeData nodeData) {
        JComponent preview = previewModel.buildPreview(nodeData);
        this.scrollPane.setViewportView(preview);
    }

    public JComponent asJComponent() { return scrollPane; }
}
