package fs.explorer.model.ftpdialog;

import fs.explorer.datasource.RemoteFilesProvider;
import fs.explorer.gui.FTPDialog;
import fs.explorer.gui.dirtree.DirTreePane;

public class FTPDialogModel {
    private final FTPDialog ftpDialog;
    private final DirTreePane dirTreePane;
    private final RemoteFilesProvider remoteFilesProvider;

    public FTPDialogModel(
            FTPDialog ftpDialog,
            DirTreePane dirTreePane,
            RemoteFilesProvider remoteFilesProvider
    ) {
        this.ftpDialog = ftpDialog;
        this.dirTreePane = dirTreePane;
        this.remoteFilesProvider = remoteFilesProvider;
    }

    public void show() {
        ftpDialog.showAndWaitResult().ifPresent(ftpDialogData -> {
            remoteFilesProvider.setConnectionInfo(ftpDialogData);
            dirTreePane.resetDataProvider(remoteFilesProvider);
        });
    }
}
