package fs.explorer.views;

import fs.explorer.controllers.StatusBarController;
import fs.explorer.controllers.ftpdialog.FTPDialogController;
import fs.explorer.controllers.MenuBarController;
import fs.explorer.controllers.DirTreeController;
import fs.explorer.providers.*;
import fs.explorer.models.dirtree.DirTreeModel;
import fs.explorer.controllers.PreviewController;
import fs.explorer.providers.preview.DefaultPreviewProvider;
import fs.explorer.providers.preview.DefaultPreviewRenderer;
import fs.explorer.providers.preview.PreviewProvider;
import fs.explorer.providers.preview.PreviewRenderer;
import fs.explorer.utils.OSInfo;

import javax.swing.*;

public class Application {
    private final MainWindow mainWindow;

    public Application() {
        FsManager localFsManager = new LocalFsManager();

        StatusBar statusBar = new StatusBar("Ready");
        StatusBarController statusBarController = new StatusBarController(statusBar);

        PreviewRenderer previewRenderer = new DefaultPreviewRenderer();

        PreviewPane previewPane = new PreviewPane();
        DefaultPreviewProvider previewProvider =
                new DefaultPreviewProvider(localFsManager, previewRenderer);
        PreviewController previewController =
                new PreviewController(previewPane, previewProvider, statusBarController);

        FsDataProvider localFsDataProvider =
                new FsDataProvider(OSInfo.getRootFsPath(), localFsManager);

        DirTreeModel dirTreeModel = new DirTreeModel();
        DirTreePane dirTreePane = new DirTreePane(dirTreeModel.getInnerTreeModel());
        DirTreeController dirTreeController = new DirTreeController(
                dirTreePane,
                dirTreeModel,
                previewController,
                statusBarController,
                localFsDataProvider
        );
        dirTreePane.setController(dirTreeController);

        MenuBar menuBar = createMenuBar(
                dirTreeController,
                previewProvider,
                localFsManager,
                localFsDataProvider,
                statusBarController
        );
        this.mainWindow = new MainWindow(
                "FsExplorer", menuBar, statusBar, dirTreePane, previewPane);
    }

    public void run() {
        SwingUtilities.invokeLater(mainWindow::show);
    }

    private MenuBar createMenuBar(
            DirTreeController dirTreeController,
            DefaultPreviewProvider previewProvider,
            FsManager localFsManager,
            FsDataProvider fsDataProvider,
            StatusBarController statusBarController
    ) {
        FTPDialog ftpDialog = new FTPDialog();
        RemoteFsManager remoteFsManager = new RemoteFsManager();
        FTPDialogController ftpDialogController = new FTPDialogController(
                ftpDialog,
                dirTreeController,
                previewProvider,
                remoteFsManager,
                statusBarController
        );
        MenuBarController controller = new MenuBarController(
                dirTreeController,
                previewProvider,
                localFsManager,
                fsDataProvider,
                ftpDialogController
        );
        return new MenuBar(controller);
    }
}
