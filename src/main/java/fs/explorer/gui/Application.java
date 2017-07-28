package fs.explorer.gui;

import fs.explorer.datasource.LocalFilesProvider;
import fs.explorer.datasource.RemoteFilesProvider;
import fs.explorer.gui.dirtree.DirTreePane;
import fs.explorer.controllers.LocalFilesExplorer;
import fs.explorer.controllers.preivew.PreviewUpdater;
import fs.explorer.controllers.RemoteFilesExplorer;

import javax.swing.*;

public class Application {
    private final MainWindow mainWindow;

    public Application() {
        PreviewPane previewPane = new PreviewPane();
        DirTreePane dirTreePane = new DirTreePane();
        PreviewUpdater previewUpdater = new PreviewUpdater(previewPane);
        dirTreePane.addTreeSelectionListener(previewUpdater);
        MenuBar menuBar = createMenuBar(dirTreePane);
        StatusBar statusBar = new StatusBar("Ready");
        this.mainWindow = new MainWindow(
                "FsExplorer", menuBar, statusBar, dirTreePane, previewPane);
    }

    public void run() {
        SwingUtilities.invokeLater(mainWindow::show);
    }

    private MenuBar createMenuBar(DirTreePane dirTreePane) {
        LocalFilesProvider localFilesProvider = new LocalFilesProvider();
        RemoteFilesProvider remoteFilesProvider = new RemoteFilesProvider();
        LocalFilesExplorer localFilesExplorer =
                new LocalFilesExplorer(dirTreePane, localFilesProvider);
        FTPDialog ftpDialog = new FTPDialog();
        RemoteFilesExplorer remoteFilesExplorer =
                new RemoteFilesExplorer(ftpDialog, dirTreePane, remoteFilesProvider);
        return new MenuBar(localFilesExplorer, remoteFilesExplorer);
    }
}
