package fs.explorer.views;

import fs.explorer.controllers.*;
import fs.explorer.controllers.FTPDialogController;
import fs.explorer.controllers.FTPInfoValidator;
import fs.explorer.models.dirtree.DirTreeModel;
import fs.explorer.providers.dirtree.AsyncFsDataProvider;
import fs.explorer.providers.dirtree.FsDataProvider;
import fs.explorer.providers.dirtree.LocalFsManager;
import fs.explorer.providers.dirtree.TreeDataProvider;
import fs.explorer.providers.dirtree.remote.RemoteFsManager;
import fs.explorer.providers.preview.AsyncPreviewProvider;
import fs.explorer.providers.preview.DefaultPreviewProvider;
import fs.explorer.providers.preview.DefaultPreviewRenderer;
import fs.explorer.providers.preview.PreviewRenderer;
import fs.explorer.utils.OSInfo;

import javax.swing.*;

public class Application {
    private final MainWindow mainWindow;

    public Application() {
        LocalFsManager localFsManager = new LocalFsManager();

        StatusBar statusBar = new StatusBar("Ready");
        StatusBarController statusBarController = new StatusBarController(statusBar);

        PreviewRenderer previewRenderer = new DefaultPreviewRenderer();

        PreviewPane previewPane = new PreviewPane();
        DefaultPreviewProvider previewProvider =
                new DefaultPreviewProvider(localFsManager, previewRenderer);
        AsyncPreviewProvider asyncPreviewProvider = new AsyncPreviewProvider(previewProvider);
        PreviewController previewController =
                new PreviewController(previewPane, asyncPreviewProvider, statusBarController);

        FsDataProvider localFsDataProvider =
                new FsDataProvider(OSInfo.getRootFsPath(), localFsManager);
        AsyncFsDataProvider asyncLocalFsDataProvider =
                new AsyncFsDataProvider(localFsDataProvider);

        DirTreeModel dirTreeModel = new DirTreeModel();
        DirTreePane dirTreePane = new DirTreePane(dirTreeModel.getInnerTreeModel());
        DirTreeController dirTreeController = new DirTreeController(
                dirTreePane,
                dirTreeModel,
                previewController,
                statusBarController,
                asyncLocalFsDataProvider
        );
        dirTreePane.setController(dirTreeController);

        FTPDialog ftpDialog = new FTPDialog();
        FTPInfoValidator ftpInfoValidator = new FTPInfoValidator();
        RemoteFsManager remoteFsManager = new RemoteFsManager();
        FsTypeSwitcher fsTypeSwitcher = new FsTypeSwitcher(
                dirTreeController,
                previewProvider,
                asyncLocalFsDataProvider,
                localFsManager,
                remoteFsManager
        );
        FTPDialogController ftpDialogController = new FTPDialogController(
                ftpDialog, ftpInfoValidator, fsTypeSwitcher, statusBarController);
        MenuBarController controller =
                new MenuBarController(fsTypeSwitcher, ftpDialogController);
        MenuBar menuBar = new MenuBar(controller);

        mainWindow = new MainWindow("FsExplorer", menuBar, statusBar, dirTreePane, previewPane);
        MainWindowController mainWindowController = new MainWindowController(
                mainWindow,
                statusBarController,
                asyncPreviewProvider,
                asyncLocalFsDataProvider,
                fsTypeSwitcher,
                remoteFsManager
        );
        mainWindow.setController(mainWindowController);
    }

    public void run() {
        SwingUtilities.invokeLater(mainWindow::show);
    }
}
