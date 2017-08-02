package fs.explorer.controllers;

import fs.explorer.providers.dirtree.remote.FTPConnectionInfo;
import fs.explorer.providers.dirtree.remote.FTPException;
import fs.explorer.views.FTPDialog;

import java.util.Optional;

public class FTPDialogController {
    private final FTPDialog ftpDialog;
    private final FTPInfoValidator ftpInfoValidator;
    private final FsTypeSwitcher fsTypeSwitcher;
    private final StatusBarController statusBarController;

    private static final String CONNECTION_FAILED = "FTP connection failed";
    private static final String CONNECTED = "Connected";

    public FTPDialogController(
            FTPDialog ftpDialog,
            FTPInfoValidator ftpInfoValidator,
            FsTypeSwitcher fsTypeSwitcher,
            StatusBarController statusBarController
    ) {
        this.ftpDialog = ftpDialog;
        this.ftpInfoValidator = ftpInfoValidator;
        this.fsTypeSwitcher = fsTypeSwitcher;
        this.statusBarController = statusBarController;
    }

    public void showAndHandleInput() {
        showAndHandleInput("");
    }

    private void showAndHandleInput(String errorMessage) {
        ftpDialog.setErrorMessage(errorMessage);
        ftpDialog.showAndWaitResult().ifPresent(this::handleInput);
    }

    private void handleInput(FTPConnectionInfo connectionInfo) {
        Optional<String> optError = ftpInfoValidator.validate(connectionInfo);
        if(optError.isPresent()) {
            showAndHandleInput(optError.get());
        } else {
            try {
                fsTypeSwitcher.switchToRemoteFs(connectionInfo);
                statusBarController.setInfoMessage(CONNECTED, connectionInfo.getHost());
            } catch (FTPException e) {
                statusBarController.setErrorMessage(CONNECTION_FAILED, e.getMessage());
            }
        }
    }
}
