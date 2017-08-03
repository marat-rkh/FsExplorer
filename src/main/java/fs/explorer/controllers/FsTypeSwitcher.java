package fs.explorer.controllers;

import fs.explorer.providers.dirtree.*;
import fs.explorer.providers.dirtree.remote.FTPConnectionInfo;
import fs.explorer.providers.dirtree.remote.FTPException;
import fs.explorer.providers.dirtree.remote.RemoteFsManager;
import fs.explorer.providers.preview.DefaultPreviewProvider;
import fs.explorer.utils.Disposable;

public class FsTypeSwitcher implements Disposable {
    private final DirTreeController dirTreeController;
    private final DefaultPreviewProvider previewProvider;
    private final TreeDataProvider localFsDataProvider;
    private final LocalFsManager localFsManager;
    private final RemoteFsManager remoteFsManager;

    private AsyncFsDataProvider asyncRemoteFsDataProvider;

    private static final FsPath remoteHostTopDir =
            new FsPath("/", FsPath.TargetType.DIRECTORY, "/");

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
        disposeCurrentRemoteFsDataProvider();
        dirTreeController.resetDataProvider(localFsDataProvider);
        previewProvider.resetFsManager(localFsManager);
    }

    public void switchToRemoteFs(FTPConnectionInfo connectionInfo) throws FTPException {
        remoteFsManager.reconnect(connectionInfo);
        disposeCurrentRemoteFsDataProvider();
        asyncRemoteFsDataProvider = new AsyncFsDataProvider(
                new FsDataProvider(remoteHostTopDir, remoteFsManager)
        );
        dirTreeController.resetDataProvider(asyncRemoteFsDataProvider);
        previewProvider.resetFsManager(remoteFsManager);
    }

    public void disposeCurrentRemoteFsDataProvider() {
        if(asyncRemoteFsDataProvider != null) {
            // TODO this `shutdown` should be done in background thread
            asyncRemoteFsDataProvider.shutdown();
            asyncRemoteFsDataProvider = null;
        }
    }

    @Override
    public void dispose() {
        disposeCurrentRemoteFsDataProvider();
    }
}
