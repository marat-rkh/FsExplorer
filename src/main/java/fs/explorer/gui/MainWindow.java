package fs.explorer.gui;

import javax.swing.*;
import java.awt.*;

public final class MainWindow {
    private final int WINDOW_WIDTH = 640;
    private final int WINDOW_HEIGHT = 480;

    private final JFrame frame;
    private final StatusBar statusBar;
    private final MenuBar menuBar;

    public MainWindow(String name) {
        frame = new JFrame(name);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        statusBar = new StatusBar("Ready");
        menuBar = new MenuBar(statusBar);

        frame.setJMenuBar(menuBar.asJMenuBar());
        frame.getContentPane().add(createSplitPane(), BorderLayout.CENTER);
    }

    public void show() {
        frame.pack();
        frame.setVisible(true);
    }

    public StatusBar getStatusBar() { return statusBar; }

    private JSplitPane createSplitPane() {
        JSplitPane treeAndPreviewPanes = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JLabel(), new JLabel());
        treeAndPreviewPanes.setOneTouchExpandable(true);
        treeAndPreviewPanes.setDividerLocation(WINDOW_WIDTH / 3);

        JSplitPane mainAndStatusPanes = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                treeAndPreviewPanes, statusBar.asJComponent());
        mainAndStatusPanes.setOneTouchExpandable(true);
        mainAndStatusPanes.setDividerLocation(9 * WINDOW_HEIGHT / 10);
        return mainAndStatusPanes;
    }
}
