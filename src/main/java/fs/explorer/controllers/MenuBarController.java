package fs.explorer.controllers;

import fs.explorer.controllers.ftpdialog.FTPDialogController;

import java.awt.event.ActionEvent;

public class MenuBarController {
    private final FsTypeSwitcher fsTypeSwitcher;
    private final FTPDialogController ftpDialogController;

    public MenuBarController(
            FsTypeSwitcher fsTypeSwitcher,
            FTPDialogController ftpDialogController
    ) {
        this.fsTypeSwitcher = fsTypeSwitcher;
        this.ftpDialogController = ftpDialogController;
    }

    public void handleExploreLocalFiles(ActionEvent e) {
        fsTypeSwitcher.switchToLocalFs();
    }

    public void handleExploreRemoteFiles(ActionEvent e) {
        ftpDialogController.showAndHandleInput();
    }
}
