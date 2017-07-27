package fs.explorer.gui;

import javax.swing.*;

public class StatusBar {
    private final JLabel label;

    public StatusBar(String idleMessage) {
        label = new JLabel(idleMessage);
    }

    public void setMessage(String message) {
        label.setText(message);
    }

    public JComponent asJComponent() { return label; }
}
