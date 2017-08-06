package fs.explorer.controllers;

import java.awt.event.ActionEvent;

public class MenuBarController {
    private final FsTypeSwitcher fsTypeSwitcher;
    private final FTPDialogController ftpDialogController;
    private final DirTreeController dirTreeController;

    public MenuBarController(
            FsTypeSwitcher fsTypeSwitcher,
            FTPDialogController ftpDialogController,
            DirTreeController dirTreeController
    ) {
        this.fsTypeSwitcher = fsTypeSwitcher;
        this.ftpDialogController = ftpDialogController;
        this.dirTreeController = dirTreeController;
    }

    public void handleExploreLocalFiles(ActionEvent e) {
        // TODO clear StatusBar before switch
        fsTypeSwitcher.switchToLocalFs();
    }

    public void handleExploreRemoteFiles(ActionEvent e) {
        ftpDialogController.showAndHandleInput();
    }

    public void handleSelectedReload(ActionEvent e) {
        dirTreeController.reloadLastSelectedNode();
    }

}
