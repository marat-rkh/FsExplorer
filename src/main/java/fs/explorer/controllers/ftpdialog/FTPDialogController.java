package fs.explorer.controllers.ftpdialog;

import fs.explorer.controllers.DirTreeController;
import fs.explorer.controllers.StatusBarController;
import fs.explorer.providers.FsDataProvider;
import fs.explorer.providers.FsPath;
import fs.explorer.providers.RemoteFsManager;
import fs.explorer.providers.preview.DefaultPreviewProvider;
import fs.explorer.views.FTPDialog;

public class FTPDialogController {
    private final FTPDialog ftpDialog;
    private final DirTreeController dirTreeController;
    private final DefaultPreviewProvider previewProvider;
    private final RemoteFsManager remoteFsManager;
    private final StatusBarController statusBarController;

    private static final String CONNECTION_FAILED = "FTP connection failed";

    public FTPDialogController(
            FTPDialog ftpDialog,
            DirTreeController dirTreeController,
            DefaultPreviewProvider previewProvider,
            RemoteFsManager remoteFsManager,
            StatusBarController statusBarController
    ) {
        this.ftpDialog = ftpDialog;
        this.dirTreeController = dirTreeController;
        this.previewProvider = previewProvider;
        this.remoteFsManager = remoteFsManager;
        this.statusBarController = statusBarController;
    }

    public void showAndHandleInput() {
        ftpDialog.showAndWaitResult().ifPresent(this::handleInput);
    }

    private void handleInput(FTPDialogData data) {
        // TODO validate input (emp user and non emp pass -- bad)
        try {
            remoteFsManager.connect(data);
        } catch (FTPException e) {
            statusBarController.setErrorMessage(CONNECTION_FAILED, e.getMessage());
            return;
        }
        statusBarController.setInfoMessage("Connected to: " + data.getServer());
        FsDataProvider remoteFsDataProvider =
                new FsDataProvider(remoteHostTopNode(), remoteFsManager);
        dirTreeController.resetDataProvider(remoteFsDataProvider);
        previewProvider.resetFsManager(remoteFsManager);
    }

    private FsPath remoteHostTopNode() {
        return new FsPath("/", /*isDirectory*/true, "/");
    }
}
