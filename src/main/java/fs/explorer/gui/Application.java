package fs.explorer.gui;

import fs.explorer.controllers.MenuBarController;
import fs.explorer.controllers.PreviewUpdater;
import fs.explorer.datasource.LocalFilesProvider;
import fs.explorer.datasource.RemoteFilesProvider;
import fs.explorer.gui.dirtree.DirTreePane;
import fs.explorer.model.ftpdialog.FTPDialogModel;
import fs.explorer.model.preview.PreviewModel;

import javax.swing.*;

public class Application {
    private final MainWindow mainWindow;

    public Application() {
        PreviewPane previewPane = new PreviewPane();
        PreviewModel previewModel = new PreviewModel(previewPane);
        PreviewUpdater previewUpdater = new PreviewUpdater(previewModel);
        DirTreePane dirTreePane = new DirTreePane();
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
        FTPDialog ftpDialog = new FTPDialog();
        FTPDialogModel ftpDialogModel =
                new FTPDialogModel(ftpDialog, dirTreePane, remoteFilesProvider);
        MenuBarController controller =
                new MenuBarController(dirTreePane, localFilesProvider, ftpDialogModel);
        return new MenuBar(controller);
    }
}
