package fs.explorer.views;

import fs.explorer.utils.CustomColors;

import javax.swing.*;
import java.awt.*;

public class StatusBar {
    private final JLabel label;

    public StatusBar(String idleMessage) {
        label = new JLabel(idleMessage);
        label.setForeground(CustomColors.DARK_GREEN);
    }

    public void setText(String message) {
        label.setText(message);
    }

    public void setTextColor(Color color) {
        label.setForeground(color);
    }

    public JComponent asJComponent() { return label; }
}
