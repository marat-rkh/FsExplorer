package fs.explorer.views;

import fs.explorer.controllers.MenuBarController;
import fs.explorer.controllers.DirTreeController;
import fs.explorer.datasource.LocalFilesProvider;
import fs.explorer.datasource.RemoteFilesProvider;
import fs.explorer.models.dirtree.DirTreeModel;
import fs.explorer.models.ftpdialog.FTPDialogModel;
import fs.explorer.models.preview.PreviewModel;

import javax.swing.*;

public class Application {
    private final MainWindow mainWindow;

    public Application() {
        PreviewPane previewPane = new PreviewPane();
        PreviewModel previewModel = new PreviewModel(previewPane);

        DirTreeModel dirTreeModel = new DirTreeModel(previewModel);
        DirTreeController dirTreeController = new DirTreeController(dirTreeModel);
        DirTreePane dirTreePane =
                new DirTreePane(dirTreeModel.getInnerTreeModel(), dirTreeController);
        dirTreeModel.setDirTreePane(dirTreePane);

        MenuBar menuBar = createMenuBar(dirTreeModel);
        StatusBar statusBar = new StatusBar("Ready");
        this.mainWindow = new MainWindow(
                "FsExplorer", menuBar, statusBar, dirTreePane, previewPane);
    }

    public void run() {
        SwingUtilities.invokeLater(mainWindow::show);
    }

    private MenuBar createMenuBar(DirTreeModel dirTreeModel) {
        LocalFilesProvider localFilesProvider = new LocalFilesProvider();
        RemoteFilesProvider remoteFilesProvider = new RemoteFilesProvider();
        FTPDialog ftpDialog = new FTPDialog();
        FTPDialogModel ftpDialogModel =
                new FTPDialogModel(ftpDialog, dirTreeModel, remoteFilesProvider);
        MenuBarController controller =
                new MenuBarController(dirTreeModel, localFilesProvider, ftpDialogModel);
        return new MenuBar(controller);
    }
}
