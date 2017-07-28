package fs.explorer.controllers;

import fs.explorer.datasource.LocalFilesProvider;
import fs.explorer.model.dirtree.DirTreeModel;
import fs.explorer.model.ftpdialog.FTPDialogModel;

import java.awt.event.ActionEvent;

public class MenuBarController {
    private final DirTreeModel dirTreeModel;
    private final LocalFilesProvider localFilesProvider;
    private final FTPDialogModel ftpDialogModel;

    public MenuBarController(
            DirTreeModel dirTreeModel,
            LocalFilesProvider localFilesProvider,
            FTPDialogModel ftpDialogModel
    ) {
        this.dirTreeModel = dirTreeModel;
        this.localFilesProvider = localFilesProvider;
        this.ftpDialogModel = ftpDialogModel;
    }

    public void handleExploreLocalFiles(ActionEvent e) {
        dirTreeModel.resetDataProvider(localFilesProvider);
    }

    public void handleExploreRemoteFiles(ActionEvent e) {
        ftpDialogModel.show();
    }
}
