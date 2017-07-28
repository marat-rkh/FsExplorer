package fs.explorer.gui;

import javax.swing.*;

public class PreviewPane {
    private final JScrollPane scrollPane;

    public PreviewPane() {
        this.scrollPane = new JScrollPane(new JLabel("Preview"));
    }

    public void updatePreview(JComponent preview) {
        this.scrollPane.setViewportView(preview);
    }

    public JComponent asJComponent() { return scrollPane; }
}
