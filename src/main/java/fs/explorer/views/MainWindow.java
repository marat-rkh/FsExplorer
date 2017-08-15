package fs.explorer.views;

import fs.explorer.controllers.MainWindowController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public final class MainWindow {
    private static final int WINDOW_WIDTH = 640;
    private static final int WINDOW_HEIGHT = 480;

    private final JFrame frame;

    public MainWindow(
            String name,
            ToolBar toolBar,
            StatusBar statusBar,
            DirTreePane dirTreePane,
            PreviewPane previewPane
    ) {
        frame = new JFrame(name);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        frame.getContentPane().add(toolBar.asJComponent(), BorderLayout.NORTH);
        frame.getContentPane().add(createMainPane(dirTreePane, previewPane), BorderLayout.CENTER);
        frame.getContentPane().add(statusBar.asJComponent(), BorderLayout.SOUTH);
    }

    public void setController(MainWindowController controller) {
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new MainWindowListener(controller));
    }

    public void show() {
        frame.pack();
        frame.setVisible(true);
    }

    public void setCursor(Cursor cursor) {
        frame.setCursor(cursor);
    }

    private JSplitPane createMainPane(DirTreePane dirTreePane, PreviewPane previewPane) {
        JSplitPane treeAndPreview = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                dirTreePane.asJComponent(),
                previewPane.asJComponent()
        );
        treeAndPreview.setOneTouchExpandable(true);
        treeAndPreview.setDividerLocation(WINDOW_WIDTH / 2);
        treeAndPreview.setResizeWeight(0.5);
        treeAndPreview.getLeftComponent().setMinimumSize(new Dimension(10, 10));
        treeAndPreview.getRightComponent().setMinimumSize(new Dimension(10, 10));
        return treeAndPreview;
    }

    private static class MainWindowListener implements WindowListener {
        private final MainWindowController mainWindowController;

        private MainWindowListener(MainWindowController mainWindowController) {
            this.mainWindowController = mainWindowController;
        }

        @Override
        public void windowOpened(WindowEvent e) {
        }

        @Override
        public void windowClosing(WindowEvent e) {
            mainWindowController.handleWindowClosing();
        }

        @Override
        public void windowClosed(WindowEvent e) {
        }

        @Override
        public void windowIconified(WindowEvent e) {
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
        }

        @Override
        public void windowActivated(WindowEvent e) {
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
        }
    }
}
