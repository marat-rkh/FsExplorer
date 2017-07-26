package fs.explorer.gui;

import javax.swing.*;
import java.awt.*;

public final class MainWindow {
    private final String name;
    private final int WINDOW_WIDTH = 640;
    private final int WINDOW_HEIGHT = 480;

    public MainWindow(String name) {
        this.name = name;
    }

    public void show() {
        SwingUtilities.invokeLater(this::createAndShow);
    }

    private void createAndShow() {
        JFrame frame = new JFrame(name);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        frame.setJMenuBar(MenuBar.create());
        frame.getContentPane().add(createSplitPane(), BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    private JSplitPane createSplitPane() {
        JSplitPane treeAndPreviewPanes = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JLabel(), new JLabel());
        treeAndPreviewPanes.setOneTouchExpandable(true);
        treeAndPreviewPanes.setDividerLocation(WINDOW_WIDTH / 3);

        JSplitPane mainAndStatusPanes = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                treeAndPreviewPanes, new JLabel("Status: OK"));
        mainAndStatusPanes.setOneTouchExpandable(true);
        mainAndStatusPanes.setDividerLocation(9 * WINDOW_HEIGHT / 10);
        return mainAndStatusPanes;
    }
}
