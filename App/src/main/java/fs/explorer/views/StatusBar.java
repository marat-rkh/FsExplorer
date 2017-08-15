package fs.explorer.views;

import fs.explorer.utils.CustomColors;

import javax.swing.*;
import java.awt.*;

public class StatusBar {
    private final JLabel label;

    public StatusBar(String idleMessage) {
        label = new JLabel(idleMessage);
        label.setMinimumSize(new Dimension(10, 20));
        label.setPreferredSize(new Dimension(10, 20));
        label.setMaximumSize(new Dimension(10, 20));
        label.setForeground(CustomColors.DARK_GREEN);
    }

    public void setText(String message) {
        label.setText(message);
    }

    public void setTextColor(Color color) {
        label.setForeground(color);
    }

    JComponent asJComponent() {
        return label;
    }
}
