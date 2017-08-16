package fs.explorer.controllers;

import fs.explorer.providers.dirtree.remote.FTPConnectionInfo;
import fs.explorer.views.FTPDialog;

import java.util.Optional;

public class FTPDialogController {
    private final FTPDialog ftpDialog;
    private final FTPInfoValidator ftpInfoValidator;
    private final FsTypeSwitcher fsTypeSwitcher;
    private final StatusBarController statusBarController;

    private FTPConnectionInfo lastConnectionInfo;

    private static final String CONNECTING = "Connecting";
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

    void showAndHandleInput() {
        showAndHandleInput("");
    }

    void handleLastInput() {
        if (lastConnectionInfo != null) {
            handleInput(lastConnectionInfo);
        } else {
            showAndHandleInput();
        }
    }

    private void showAndHandleInput(String errorMessage) {
        ftpDialog.setErrorMessage(errorMessage);
        ftpDialog.showAndWaitResult().ifPresent(this::handleInput);
    }

    private void handleInput(FTPConnectionInfo connectionInfo) {
        lastConnectionInfo = connectionInfo;
        Optional<String> optError = ftpInfoValidator.validate(connectionInfo);
        if (optError.isPresent()) {
            showAndHandleInput(optError.get());
        } else {
            FsTypeSwitchProgressHandler handler = new FsTypeSwitchProgressHandler() {
                @Override
                public void onComplete() {
                    statusBarController.setInfoMessage(CONNECTED, connectionInfo.getHost());
                }

                @Override
                public void onFail(String errorMessage) {
                    statusBarController.setErrorMessage(CONNECTION_FAILED, errorMessage);
                }
            };
            statusBarController.setProgressMessage(CONNECTING, connectionInfo.getHost());
            fsTypeSwitcher.switchToRemoteFs(connectionInfo, handler);
        }
    }
}
