package fs.explorer.controllers;

import fs.explorer.controllers.ftpdialog.FTPDialogController;
import fs.explorer.providers.FsDataProvider;

import java.awt.event.ActionEvent;

public class MenuBarController {
    private final DirTreeController dirTreeController;
    private final FsDataProvider fsDataProvider;
    private final FTPDialogController ftpDialogController;

    public MenuBarController(
            DirTreeController dirTreeController,
            FsDataProvider fsDataProvider,
            FTPDialogController ftpDialogController
    ) {
        this.dirTreeController = dirTreeController;
        this.fsDataProvider = fsDataProvider;
        this.ftpDialogController = ftpDialogController;
    }

    public void handleExploreLocalFiles(ActionEvent e) {
        dirTreeController.resetDataProvider(fsDataProvider);
    }

    public void handleExploreRemoteFiles(ActionEvent e) {
        ftpDialogController.showAndHandleInput();
    }
}
