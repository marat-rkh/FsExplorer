package fs.explorer.controllers;

import fs.explorer.providers.dirtree.*;
import fs.explorer.providers.dirtree.archives.ArchivesManager;
import fs.explorer.providers.dirtree.local.LocalFsManager;
import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.path.TargetType;
import fs.explorer.providers.dirtree.remote.FTPConnectionInfo;
import fs.explorer.providers.dirtree.remote.FTPException;
import fs.explorer.providers.dirtree.remote.RemoteFsManager;
import fs.explorer.providers.preview.DefaultPreviewProvider;
import fs.explorer.utils.Disposable;
import fs.explorer.utils.OSInfo;

public class FsTypeSwitcher implements Disposable {
    private final DirTreeController dirTreeController;
    private final DefaultPreviewProvider previewProvider;
    private final LocalFsManager localFsManager;
    private final ArchivesManager archivesManager;

    private DefaultAsyncFsDataProvider asyncFsDataProvider;

    private static final FsPath localDriveTopDir = OSInfo.getRootFsPath();
    private static final FsPath remoteHostTopDir = new FsPath("/", TargetType.DIRECTORY, "/");

    public FsTypeSwitcher(
            DirTreeController dirTreeController,
            DefaultPreviewProvider previewProvider,
            LocalFsManager localFsManager,
            ArchivesManager archivesManager
    ) {
        this.dirTreeController = dirTreeController;
        this.previewProvider = previewProvider;
        this.localFsManager = localFsManager;
        this.archivesManager = archivesManager;
    }

    @Override
    public void dispose() {
        disposeCurrentFsDataProvider();
    }

    void switchToLocalFs() {
        switchFs(localDriveTopDir, localFsManager);
    }

    void switchToRemoteFs(FTPConnectionInfo connectionInfo) throws FTPException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(connectionInfo);
        remoteFsManager.checkConnection();
        switchFs(remoteHostTopDir, remoteFsManager);
    }

    void disposeCurrentFsDataProvider() {
        if (asyncFsDataProvider != null) {
            // TODO this `dispose` should be done in background thread
            asyncFsDataProvider.dispose();
            asyncFsDataProvider = null;
        }
    }

    private void switchFs(FsPath topDir, FsManager fsManager) {
        disposeCurrentFsDataProvider();
        asyncFsDataProvider = new DefaultAsyncFsDataProvider(
                new DefaultFsDataProvider(topDir, fsManager, archivesManager)
        );
        dirTreeController.resetDataProvider(asyncFsDataProvider);
        previewProvider.resetFsManager(fsManager);
    }
}
