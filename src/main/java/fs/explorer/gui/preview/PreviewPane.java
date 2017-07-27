package fs.explorer.gui.preview;

import fs.explorer.datasource.TreeNodeData;

import javax.swing.*;

public class PreviewPane {
    private final JScrollPane scrollPane;

    public PreviewPane() {
        this.scrollPane = new JScrollPane(new JLabel("Preview"));
    }

    public void updatePreview(TreeNodeData nodeData) {
        // TODO load preview data
        PreviewData previewData = new PreviewData(
                nodeData.toString().getBytes(), PreviewData.Type.TEXT);
        this.scrollPane.setViewportView(PreviewRenderer.render(previewData));
    }

    public JComponent asJComponent() { return scrollPane; }
}
