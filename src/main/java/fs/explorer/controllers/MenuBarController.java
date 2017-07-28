package fs.explorer.controllers;

import fs.explorer.datasource.LocalFilesProvider;
import fs.explorer.gui.dirtree.DirTreePane;
import fs.explorer.model.ftpdialog.FTPDialogModel;

import java.awt.event.ActionEvent;

public class MenuBarController {
    private final DirTreePane dirTreePane;
    private final LocalFilesProvider localFilesProvider;
    private final FTPDialogModel ftpDialogModel;

    public MenuBarController(
            DirTreePane dirTreePane,
            LocalFilesProvider localFilesProvider,
            FTPDialogModel ftpDialogModel
    ) {
        this.dirTreePane = dirTreePane;
        this.localFilesProvider = localFilesProvider;
        this.ftpDialogModel = ftpDialogModel;
    }

    public void handleExploreLocalFiles(ActionEvent e) {
        dirTreePane.resetDataProvider(localFilesProvider);
    }

    public void handleExploreRemoteFiles(ActionEvent e) {
        ftpDialogModel.show();
    }
}
