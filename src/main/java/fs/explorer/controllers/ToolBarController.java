package fs.explorer.controllers;

import java.awt.event.ActionEvent;

public class ToolBarController {
    private final FsTypeSwitcher fsTypeSwitcher;
    private final FTPDialogController ftpDialogController;
    private final DirTreeController dirTreeController;
    private final StatusBarController statusBarController;

    private static final String EXPLORING_LOCAL_DRIVE = "Exploring local drive";

    public ToolBarController(
            FsTypeSwitcher fsTypeSwitcher,
            FTPDialogController ftpDialogController,
            DirTreeController dirTreeController,
            StatusBarController statusBarController
    ) {
        this.fsTypeSwitcher = fsTypeSwitcher;
        this.ftpDialogController = ftpDialogController;
        this.dirTreeController = dirTreeController;
        this.statusBarController = statusBarController;
    }

    public void handleExploreLocalFiles(ActionEvent e) {
        fsTypeSwitcher.switchToLocalFs();
        statusBarController.setInfoMessage(EXPLORING_LOCAL_DRIVE);
    }

    public void handleExploreRemoteFiles(ActionEvent e) {
        ftpDialogController.showAndHandleInput();
    }

    public void handleReconnectToLastRemoteHost(ActionEvent e) {
        ftpDialogController.handleLastInput();
    }

    public void handleSelectedReload(ActionEvent e) {
        dirTreeController.reloadLastSelectedNode();
    }

}
