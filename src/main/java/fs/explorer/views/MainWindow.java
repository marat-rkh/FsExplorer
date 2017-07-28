package fs.explorer.views;

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
            DirTreePane dirTreePane,
            PreviewPane previewPane
    ) {
        frame = new JFrame(name);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        frame.setJMenuBar(menuBar.asJMenuBar());
        JSplitPane mainPane = createMainPane(statusBar, dirTreePane, previewPane);
        frame.getContentPane().add(mainPane, BorderLayout.CENTER);
    }

    public void show() {
        frame.pack();
        frame.setVisible(true);
    }

    private JSplitPane createMainPane(
            StatusBar statusBar,
            DirTreePane dirTreePane,
            PreviewPane previewPane
    ) {
        JSplitPane treeAndPreview = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                dirTreePane.asJComponent(),
                previewPane.asJComponent()
        );
        treeAndPreview.setOneTouchExpandable(true);
        treeAndPreview.setDividerLocation(WINDOW_WIDTH / 3);

        JSplitPane mainAndStatusBar = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                treeAndPreview,
                statusBar.asJComponent()
        );
        mainAndStatusBar.setOneTouchExpandable(true);
        mainAndStatusBar.setDividerLocation(9 * WINDOW_HEIGHT / 10);
        return mainAndStatusBar;
    }
}
