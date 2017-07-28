package fs.explorer.controllers;

import fs.explorer.controllers.ftpdialog.FTPDialogController;
import fs.explorer.datasource.LocalFilesProvider;

import java.awt.event.ActionEvent;

public class MenuBarController {
    private final DirTreeController dirTreeController;
    private final LocalFilesProvider localFilesProvider;
    private final FTPDialogController ftpDialogController;

    public MenuBarController(
            DirTreeController dirTreeController,
            LocalFilesProvider localFilesProvider,
            FTPDialogController ftpDialogController
    ) {
        this.dirTreeController = dirTreeController;
        this.localFilesProvider = localFilesProvider;
        this.ftpDialogController = ftpDialogController;
    }

    public void handleExploreLocalFiles(ActionEvent e) {
        dirTreeController.resetDataProvider(localFilesProvider);
    }

    public void handleExploreRemoteFiles(ActionEvent e) {
        ftpDialogController.showAndHandleInput();
    }
}
