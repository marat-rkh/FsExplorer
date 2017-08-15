package fs.explorer.controllers;

import fs.explorer.utils.DelayedUIAction;

import java.awt.event.ActionEvent;

public class ToolBarController {
    private final FsTypeSwitcher fsTypeSwitcher;
    private final FTPDialogController ftpDialogController;
    private final DirTreeController dirTreeController;
    private final StatusBarController statusBarController;

    private final DelayedUIAction delayedReconnectToRemoteFs;
    private final DelayedUIAction delayedExploreRemoteFiles;

    private static final int DEFAULT_RECONNECT_DELAY_MILLISECONDS = 200;
    private static final int DEFAULT_EXPLORE_REMOTE_FILES_DELAY_MILLISECONDS = 200;
    private static final String EXPLORING_LOCAL_DRIVE = "Exploring local drive";

    public ToolBarController(
            FsTypeSwitcher fsTypeSwitcher,
            FTPDialogController ftpDialogController,
            DirTreeController dirTreeController,
            StatusBarController statusBarController
    ) {
        this.fsTypeSwitcher = fsTypeSwitcher;
        this.ftpDialogController = ftpDialogController;
        this.dirTreeController = dirTreeController;
        this.statusBarController = statusBarController;
        this.delayedReconnectToRemoteFs = new DelayedUIAction(DEFAULT_RECONNECT_DELAY_MILLISECONDS);
        this.delayedExploreRemoteFiles = new DelayedUIAction(
                DEFAULT_EXPLORE_REMOTE_FILES_DELAY_MILLISECONDS);
    }

    ToolBarController(
            FsTypeSwitcher fsTypeSwitcher,
            FTPDialogController ftpDialogController,
            DirTreeController dirTreeController,
            StatusBarController statusBarController,
            int reconnectDelayMilliseconds,
            int exploreRemoteFilesDelayMilliseconds
    ) {
        this.fsTypeSwitcher = fsTypeSwitcher;
        this.ftpDialogController = ftpDialogController;
        this.dirTreeController = dirTreeController;
        this.statusBarController = statusBarController;
        this.delayedReconnectToRemoteFs = new DelayedUIAction(reconnectDelayMilliseconds);
        this.delayedExploreRemoteFiles = new DelayedUIAction(exploreRemoteFilesDelayMilliseconds);
    }

    public void handleExploreLocalFiles(ActionEvent e) {
        fsTypeSwitcher.switchToLocalFs();
        statusBarController.setInfoMessage(EXPLORING_LOCAL_DRIVE);
    }

    public void handleExploreRemoteFiles(ActionEvent e) {
        delayedExploreRemoteFiles.execute(ftpDialogController::showAndHandleInput);
    }

    public void handleReconnectToLastRemoteHost(ActionEvent e) {
        delayedReconnectToRemoteFs.execute(ftpDialogController::handleLastInput);
    }

    public void handleSelectedReload(ActionEvent e) {
        dirTreeController.reloadLastSelectedNode();
    }

}
