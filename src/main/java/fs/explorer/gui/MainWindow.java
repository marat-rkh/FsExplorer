package fs.explorer.gui;

import javax.swing.*;
import java.awt.*;

public final class MainWindow {
    private final int WINDOW_WIDTH = 640;
    private final int WINDOW_HEIGHT = 480;

    private final JFrame frame;
    private final StatusBar statusBar;
    private final DirTree dirTree;
    private final MenuBar menuBar;

    public MainWindow(String name) {
        frame = new JFrame(name);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        statusBar = new StatusBar("Ready");
        dirTree = new DirTree();
        menuBar = new MenuBar(statusBar, dirTree);

        frame.setJMenuBar(menuBar.asJMenuBar());
        frame.getContentPane().add(createMainPane(), BorderLayout.CENTER);
    }

    public void show() {
        frame.pack();
        frame.setVisible(true);
    }

    private JSplitPane createMainPane() {
        JSplitPane treeAndPreviewPanes = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, createDirTreePane(), new JLabel());
        treeAndPreviewPanes.setOneTouchExpandable(true);
        treeAndPreviewPanes.setDividerLocation(WINDOW_WIDTH / 3);

        JSplitPane mainAndStatusPanes = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, treeAndPreviewPanes, statusBar.asJComponent());
        mainAndStatusPanes.setOneTouchExpandable(true);
        mainAndStatusPanes.setDividerLocation(9 * WINDOW_HEIGHT / 10);
        return mainAndStatusPanes;
    }

    private JScrollPane createDirTreePane() {
        return new JScrollPane(dirTree.asJComponent());
    }
}
