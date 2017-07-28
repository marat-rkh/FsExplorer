package fs.explorer.model.ftpdialog;

import fs.explorer.datasource.RemoteFilesProvider;
import fs.explorer.gui.FTPDialog;
import fs.explorer.model.dirtree.DirTreeModel;

public class FTPDialogModel {
    private final FTPDialog ftpDialog;
    private final DirTreeModel dirTreeModel;
    private final RemoteFilesProvider remoteFilesProvider;

    public FTPDialogModel(
            FTPDialog ftpDialog,
            DirTreeModel dirTreeModel,
            RemoteFilesProvider remoteFilesProvider
    ) {
        this.ftpDialog = ftpDialog;
        this.dirTreeModel = dirTreeModel;
        this.remoteFilesProvider = remoteFilesProvider;
    }

    public void show() {
        ftpDialog.showAndWaitResult().ifPresent(ftpDialogData -> {
            remoteFilesProvider.setConnectionInfo(ftpDialogData);
            dirTreeModel.resetDataProvider(remoteFilesProvider);
        });
    }
}
