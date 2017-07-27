package fs.explorer.gui.preview;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class PreviewPane {
    private final JScrollPane scrollPane;

    public PreviewPane() {
        this.scrollPane = new JScrollPane(new JLabel("Preview"));
    }

    public void updatePreview(DefaultMutableTreeNode node) {
        // TODO load preview data
        PreviewData previewData = new PreviewData(
                node.toString().getBytes(), PreviewData.Type.TEXT);
        this.scrollPane.setViewportView(PreviewRenderer.render(previewData));
    }

    public JComponent asJComponent() { return scrollPane; }
}
