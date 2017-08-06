package fs.explorer.views;

import fs.explorer.controllers.*;
import fs.explorer.controllers.FTPDialogController;
import fs.explorer.controllers.FTPInfoValidator;
import fs.explorer.models.dirtree.DirTreeModel;
import fs.explorer.providers.dirtree.AsyncFsDataProvider;
import fs.explorer.providers.dirtree.FsDataProvider;
import fs.explorer.providers.dirtree.local.LocalFsManager;
import fs.explorer.providers.dirtree.archives.ArchivesManager;
import fs.explorer.providers.dirtree.archives.ArchivesReader;
import fs.explorer.providers.dirtree.remote.RemoteFsManager;
import fs.explorer.providers.preview.AsyncPreviewProvider;
import fs.explorer.providers.preview.DefaultPreviewProvider;
import fs.explorer.providers.preview.DefaultPreviewRenderer;
import fs.explorer.providers.preview.PreviewRenderer;
import fs.explorer.utils.Disposable;
import fs.explorer.utils.OSInfo;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Application {
    private final MainWindow mainWindow;

    // TODO decide how to handle this exception
    public Application() throws IOException {
        List<Disposable> disposables = new ArrayList<>();

        LocalFsManager localFsManager = new LocalFsManager();

        ArchivesReader archivesReader = new ArchivesReader();
        ArchivesManager archivesManager = new ArchivesManager(archivesReader);
        disposables.add(archivesManager);

        StatusBar statusBar = new StatusBar("Ready");
        StatusBarController statusBarController = new StatusBarController(statusBar);

        PreviewRenderer previewRenderer = new DefaultPreviewRenderer();

        PreviewPane previewPane = new PreviewPane();
        DefaultPreviewProvider previewProvider =
                new DefaultPreviewProvider(localFsManager, archivesManager, previewRenderer);
        AsyncPreviewProvider asyncPreviewProvider = new AsyncPreviewProvider(previewProvider);
        disposables.add(asyncPreviewProvider);
        PreviewController previewController =
                new PreviewController(previewPane, asyncPreviewProvider, statusBarController);

        FsDataProvider localFsDataProvider =
                new FsDataProvider(OSInfo.getRootFsPath(), localFsManager, archivesManager);
        AsyncFsDataProvider asyncLocalFsDataProvider =
                new AsyncFsDataProvider(localFsDataProvider);
        disposables.add(asyncLocalFsDataProvider);

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
        disposables.add(remoteFsManager);
        FsTypeSwitcher fsTypeSwitcher = new FsTypeSwitcher(
                dirTreeController,
                previewProvider,
                asyncLocalFsDataProvider,
                localFsManager,
                remoteFsManager,
                archivesManager
        );
        disposables.add(fsTypeSwitcher);
        FTPDialogController ftpDialogController = new FTPDialogController(
                ftpDialog, ftpInfoValidator, fsTypeSwitcher, statusBarController);
        MenuBarController controller =
                new MenuBarController(fsTypeSwitcher, ftpDialogController);
        MenuBar menuBar = new MenuBar(controller);

        mainWindow = new MainWindow("FsExplorer", menuBar, statusBar, dirTreePane, previewPane);
        MainWindowController mainWindowController = new MainWindowController(
                mainWindow,
                statusBarController,
                disposables
        );
        mainWindow.setController(mainWindowController);
    }

    public void run() {
        SwingUtilities.invokeLater(mainWindow::show);
    }
}
