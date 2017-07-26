package fs.explorer.gui;

import javax.swing.*;
import java.awt.*;

public final class MainWindow {
    private final String name;

    public MainWindow(String name) {
        this.name = name;
    }

    public void show() {
        javax.swing.SwingUtilities.invokeLater(this::createAndShow);
    }

    private void createAndShow() {
        JFrame frame = new JFrame(name);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLabel yellowLabel = new JLabel();
        yellowLabel.setOpaque(true);
        yellowLabel.setBackground(new Color(248, 213, 131));
        yellowLabel.setPreferredSize(new Dimension(200, 180));

        frame.setJMenuBar(MenuBar.create());
        frame.getContentPane().add(yellowLabel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }
}
