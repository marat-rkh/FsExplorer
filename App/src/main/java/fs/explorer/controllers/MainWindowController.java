package fs.explorer.controllers;

import fs.explorer.utils.Disposable;
import fs.explorer.views.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainWindowController {
    private final MainWindow mainWindow;
    private final StatusBarController statusBarController;
    private final List<Disposable> resources;

    private static final String CLOSING_APP = "Closing application...";

    public MainWindowController(
            MainWindow mainWindow,
            StatusBarController statusBarController,
            List<Disposable> resources
    ) {
        this.mainWindow = mainWindow;
        this.statusBarController = statusBarController;
        this.resources = resources;
    }

    public void handleWindowClosing() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                resources.forEach(Disposable::dispose);
                return null;
            }

            @Override
            protected void done() {
                System.exit(0);
            }
        };
        statusBarController.setProgressMessage(CLOSING_APP);
        mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        worker.execute();
    }
}
