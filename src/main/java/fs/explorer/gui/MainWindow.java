package fs.explorer.gui;

import fs.explorer.gui.preview.PreviewPane;

import javax.swing.*;
import java.awt.*;

public final class MainWindow {
    private final int WINDOW_WIDTH = 640;
    private final int WINDOW_HEIGHT = 480;

    private final JFrame frame;

    public MainWindow(
            String name,
            MenuBar menuBar,
            StatusBar statusBar,
            DirTree dirTree,
            PreviewPane previewPane
    ) {
        frame = new JFrame(name);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        frame.setJMenuBar(menuBar.asJMenuBar());
        JSplitPane mainPane = createMainPane(statusBar, dirTree, previewPane);
        frame.getContentPane().add(mainPane, BorderLayout.CENTER);
    }

    public void show() {
        frame.pack();
        frame.setVisible(true);
    }

    private JSplitPane createMainPane(
            StatusBar statusBar,
            DirTree dirTree,
            PreviewPane previewPane
    ) {
        JSplitPane treeAndPreviewPanes = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createDirTreePane(dirTree),
                previewPane.asJComponent()
        );
        treeAndPreviewPanes.setOneTouchExpandable(true);
        treeAndPreviewPanes.setDividerLocation(WINDOW_WIDTH / 3);

        JSplitPane mainAndStatusPanes = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, treeAndPreviewPanes, statusBar.asJComponent());
        mainAndStatusPanes.setOneTouchExpandable(true);
        mainAndStatusPanes.setDividerLocation(9 * WINDOW_HEIGHT / 10);
        return mainAndStatusPanes;
    }

    private JScrollPane createDirTreePane(DirTree dirTree) {
        return new JScrollPane(dirTree.asJComponent());
    }
}
