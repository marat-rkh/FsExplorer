package fs.explorer.controllers;

import fs.explorer.providers.dirtree.AsyncFsDataProvider;
import fs.explorer.providers.dirtree.remote.FTPException;
import fs.explorer.providers.dirtree.remote.RemoteFsManager;
import fs.explorer.providers.preview.AsyncPreviewProvider;
import fs.explorer.views.MainWindow;

import javax.swing.*;
import java.awt.*;

public class MainWindowController {
    private final MainWindow mainWindow;
    private final StatusBarController statusBarController;

    private final AsyncPreviewProvider asyncPreviewProvider;
    private final AsyncFsDataProvider asyncLocalFsDataProvider;
    private final FsTypeSwitcher fsTypeSwitcher;
    private final RemoteFsManager remoteFsManager;

    private static final String CLOSING_APP = "Closing application...";

    public MainWindowController(
            MainWindow mainWindow,
            StatusBarController statusBarController,
            // TODO all these classes should implement Disposable and be passed as List
            AsyncPreviewProvider asyncPreviewProvider,
            AsyncFsDataProvider asyncLocalFsDataProvider,
            FsTypeSwitcher fsTypeSwitcher,
            RemoteFsManager remoteFsManager
    ) {
        this.mainWindow = mainWindow;
        this.statusBarController = statusBarController;
        this.asyncPreviewProvider = asyncPreviewProvider;
        this.asyncLocalFsDataProvider = asyncLocalFsDataProvider;
        this.fsTypeSwitcher = fsTypeSwitcher;
        this.remoteFsManager = remoteFsManager;
    }

    public void handleWindowClosing() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                cleanupResources();
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

    private void cleanupResources() {
        asyncPreviewProvider.shutdown();
        asyncLocalFsDataProvider.shutdown();
        fsTypeSwitcher.disposeCurrentRemoteFsDataProvider();
        try {
            remoteFsManager.disconnect();
        } catch (FTPException e) {
            // do nothing
        }
    }
}
