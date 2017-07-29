package fs.explorer.controllers.ftpdialog;

import fs.explorer.controllers.DirTreeController;
import fs.explorer.providers.RemoteFilesProvider;
import fs.explorer.views.FTPDialog;

public class FTPDialogController {
    private final FTPDialog ftpDialog;
    private final DirTreeController dirTreeController;
    private final RemoteFilesProvider remoteFilesProvider;

    public FTPDialogController(
            FTPDialog ftpDialog,
            DirTreeController dirTreeController,
            RemoteFilesProvider remoteFilesProvider
    ) {
        this.ftpDialog = ftpDialog;
        this.dirTreeController = dirTreeController;
        this.remoteFilesProvider = remoteFilesProvider;
    }

    public void showAndHandleInput() {
        ftpDialog.showAndWaitResult().ifPresent(ftpDialogData -> {
            remoteFilesProvider.setConnectionInfo(ftpDialogData);
            dirTreeController.resetDataProvider(remoteFilesProvider);
        });
    }
}
