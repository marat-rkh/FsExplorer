package fs.explorer.views;

import fs.explorer.controllers.StatusBarController;
import fs.explorer.controllers.ftpdialog.FTPDialogController;
import fs.explorer.controllers.MenuBarController;
import fs.explorer.controllers.DirTreeController;
import fs.explorer.providers.LocalFilesProvider;
import fs.explorer.providers.RemoteFilesProvider;
import fs.explorer.models.dirtree.DirTreeModel;
import fs.explorer.controllers.preview.PreviewController;

import javax.swing.*;

public class Application {
    private final MainWindow mainWindow;

    public Application() {
        PreviewPane previewPane = new PreviewPane();
        PreviewController previewController = new PreviewController(previewPane);

        LocalFilesProvider localFilesProvider = new LocalFilesProvider();

        StatusBar statusBar = new StatusBar("Ready");
        StatusBarController statusBarController = new StatusBarController(statusBar);

        DirTreeModel dirTreeModel = new DirTreeModel();
        DirTreePane dirTreePane = new DirTreePane(dirTreeModel.getInnerTreeModel());
        DirTreeController dirTreeController = new DirTreeController(
                dirTreePane,
                dirTreeModel,
                previewController,
                statusBarController,
                localFilesProvider
        );
        dirTreePane.setController(dirTreeController);

        MenuBar menuBar = createMenuBar(dirTreeController, localFilesProvider);
        this.mainWindow = new MainWindow(
                "FsExplorer", menuBar, statusBar, dirTreePane, previewPane);
    }

    public void run() {
        SwingUtilities.invokeLater(mainWindow::show);
    }

    private MenuBar createMenuBar(
            DirTreeController dirTreeController,
            LocalFilesProvider localFilesProvider
    ) {
        RemoteFilesProvider remoteFilesProvider = new RemoteFilesProvider();
        FTPDialog ftpDialog = new FTPDialog();
        FTPDialogController ftpDialogController =
                new FTPDialogController(ftpDialog, dirTreeController, remoteFilesProvider);
        MenuBarController controller = new MenuBarController(
                dirTreeController, localFilesProvider, ftpDialogController);
        return new MenuBar(controller);
    }
}
