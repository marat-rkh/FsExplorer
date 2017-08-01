package fs.explorer.controllers;

import fs.explorer.controllers.ftpdialog.FTPDialogController;
import fs.explorer.providers.FsDataProvider;
import fs.explorer.providers.FsManager;
import fs.explorer.providers.preview.DefaultPreviewProvider;

import java.awt.event.ActionEvent;

public class MenuBarController {
    private final DirTreeController dirTreeController;
    private final DefaultPreviewProvider previewProvider;
    private final FsManager localFsManager;
    private final FsDataProvider localFsDataProvider;
    private final FTPDialogController ftpDialogController;

    public MenuBarController(
            DirTreeController dirTreeController,
            DefaultPreviewProvider previewProvider,
            FsManager localFsManager,
            FsDataProvider localFsDataProvider,
            FTPDialogController ftpDialogController
    ) {
        this.dirTreeController = dirTreeController;
        this.previewProvider = previewProvider;
        this.localFsManager = localFsManager;
        this.localFsDataProvider = localFsDataProvider;
        this.ftpDialogController = ftpDialogController;
    }

    public void handleExploreLocalFiles(ActionEvent e) {
        // TODO create FsTypeSwitcher and move all this there
        dirTreeController.resetDataProvider(localFsDataProvider);
        previewProvider.resetFsManager(localFsManager);
    }

    public void handleExploreRemoteFiles(ActionEvent e) {
        ftpDialogController.showAndHandleInput();
    }
}
