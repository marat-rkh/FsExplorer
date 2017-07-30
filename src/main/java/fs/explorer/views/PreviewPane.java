package fs.explorer.views;

import javax.swing.*;

public class PreviewPane {
    private final JScrollPane scrollPane;
    private final JLabel defaultPreview;

    public PreviewPane() {
        defaultPreview = new JLabel("No preview", JLabel.CENTER);
        this.scrollPane = new JScrollPane(defaultPreview);
    }

    public void updatePreview(JComponent preview) {
        scrollPane.setViewportView(preview);
    }

    public void showDefaultPreview() {
        scrollPane.setViewportView(defaultPreview);
    }

    public JComponent asJComponent() { return scrollPane; }
}
