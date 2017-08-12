package fs.explorer.views;

import javax.swing.*;
import java.awt.*;

public class PreviewPane {
    private final JPanel panel;
    private final JLabel defaultPreview;

    public PreviewPane() {
        defaultPreview = new JLabel("No preview", JLabel.CENTER);
        this.panel = new JPanel(new BorderLayout());
    }

    public void updatePreview(JComponent preview) {
        panel.removeAll();
        panel.add(preview);
    }

    public void showDefaultPreview() {
        panel.removeAll();
        panel.add(defaultPreview);
    }

    JComponent asJComponent() { return panel; }

    public Dimension getSize() {
        return panel.getSize();
    }
}