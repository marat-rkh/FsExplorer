package fs.explorer.controllers.ftpdialog;

import fs.explorer.controllers.FsTypeSwitcher;
import fs.explorer.controllers.StatusBarController;
import fs.explorer.views.FTPDialog;

public class FTPDialogController {
    private final FTPDialog ftpDialog;
    private final FsTypeSwitcher fsTypeSwitcher;
    private final StatusBarController statusBarController;

    private static final String CONNECTION_FAILED = "FTP connection failed";

    public FTPDialogController(
            FTPDialog ftpDialog,
            FsTypeSwitcher fsTypeSwitcher,
            StatusBarController statusBarController
    ) {
        this.ftpDialog = ftpDialog;
        this.fsTypeSwitcher = fsTypeSwitcher;
        this.statusBarController = statusBarController;
    }

    public void showAndHandleInput() {
        ftpDialog.showAndWaitResult().ifPresent(this::handleInput);
    }

    private void handleInput(FTPConnectionInfo connectionInfo) {
        // TODO validate input (emp user and non emp pass -- bad)
        try {
            fsTypeSwitcher.switchToRemoteFs(connectionInfo);
        } catch (FTPException e) {
            statusBarController.setErrorMessage(CONNECTION_FAILED, e.getMessage());
            return;
        }
        statusBarController.setInfoMessage("Connected to: " + connectionInfo.getHost());
    }
}
