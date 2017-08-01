package fs.explorer.controllers;

import fs.explorer.providers.FTPConnectionInfo;
import fs.explorer.providers.FTPException;
import fs.explorer.providers.*;
import fs.explorer.providers.preview.DefaultPreviewProvider;

public class FsTypeSwitcher {
    private final DirTreeController dirTreeController;
    private final DefaultPreviewProvider previewProvider;
    private final TreeDataProvider localFsDataProvider;
    private final LocalFsManager localFsManager;
    private final RemoteFsManager remoteFsManager;

    private static final FsPath remoteHostTopDir =
            new FsPath("/", /*isDirectory*/true, "/");

    public FsTypeSwitcher(
            DirTreeController dirTreeController,
            DefaultPreviewProvider previewProvider,
            TreeDataProvider localFsDataProvider,
            LocalFsManager localFsManager,
            RemoteFsManager remoteFsManager
    ) {
        this.dirTreeController = dirTreeController;
        this.previewProvider = previewProvider;
        this.localFsDataProvider = localFsDataProvider;
        this.localFsManager = localFsManager;
        this.remoteFsManager = remoteFsManager;
    }

    public void switchToLocalFs() {
        dirTreeController.resetDataProvider(localFsDataProvider);
        previewProvider.resetFsManager(localFsManager);
    }

    public void switchToRemoteFs(FTPConnectionInfo connectionInfo) throws FTPException {
        remoteFsManager.connect(connectionInfo);
        FsDataProvider remoteFsDataProvider =
                new FsDataProvider(remoteHostTopDir, remoteFsManager);
        dirTreeController.resetDataProvider(remoteFsDataProvider);
        previewProvider.resetFsManager(remoteFsManager);
    }
}
