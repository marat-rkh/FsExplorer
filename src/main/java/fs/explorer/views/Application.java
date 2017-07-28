package fs.explorer.views;

import fs.explorer.controllers.FTPDialogController;
import fs.explorer.controllers.MenuBarController;
import fs.explorer.controllers.DirTreeController;
import fs.explorer.datasource.LocalFilesProvider;
import fs.explorer.datasource.RemoteFilesProvider;
import fs.explorer.models.dirtree.DirTreeModel;
import fs.explorer.models.preview.PreviewModel;

import javax.swing.*;

public class Application {
    private final MainWindow mainWindow;

    public Application() {
        PreviewPane previewPane = new PreviewPane();
        PreviewModel previewModel = new PreviewModel(previewPane);

        DirTreeModel dirTreeModel = new DirTreeModel();
        DirTreePane dirTreePane = new DirTreePane(dirTreeModel.getInnerTreeModel());
        DirTreeController dirTreeController =
                new DirTreeController(dirTreePane, dirTreeModel, previewModel);
        dirTreePane.setController(dirTreeController);

        MenuBar menuBar = createMenuBar(dirTreeController);
        StatusBar statusBar = new StatusBar("Ready");
        this.mainWindow = new MainWindow(
                "FsExplorer", menuBar, statusBar, dirTreePane, previewPane);
    }

    public void run() {
        SwingUtilities.invokeLater(mainWindow::show);
    }

    private MenuBar createMenuBar(DirTreeController dirTreeController) {
        LocalFilesProvider localFilesProvider = new LocalFilesProvider();
        RemoteFilesProvider remoteFilesProvider = new RemoteFilesProvider();
        FTPDialog ftpDialog = new FTPDialog();
        FTPDialogController ftpDialogController =
                new FTPDialogController(ftpDialog, dirTreeController, remoteFilesProvider);
        MenuBarController controller = new MenuBarController(
                dirTreeController, localFilesProvider, ftpDialogController);
        return new MenuBar(controller);
    }
}
