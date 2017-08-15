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

import javax.swing.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class FsTypeSwitcher implements Disposable {
    private final DirTreeController dirTreeController;
    private final DefaultPreviewProvider previewProvider;
    private final LocalFsManager localFsManager;
    private final ArchivesManager archivesManager;
    private final boolean switchToRemoteFsAsynchronously;

    private DefaultAsyncFsDataProvider asyncFsDataProvider;

    private SwingWorker<RemoteFsManager, Void> asyncRemoteFsSwitcher;

    private static final FsPath localDriveTopDir = OSInfo.getRootFsPath();
    private static final FsPath remoteHostTopDir = new FsPath("/", TargetType.DIRECTORY, "/");

    public FsTypeSwitcher(
            DirTreeController dirTreeController,
            DefaultPreviewProvider previewProvider,
            LocalFsManager localFsManager,
            ArchivesManager archivesManager
    ) {
        this(dirTreeController, previewProvider, localFsManager, archivesManager, true);
    }

    FsTypeSwitcher(
            DirTreeController dirTreeController,
            DefaultPreviewProvider previewProvider,
            LocalFsManager localFsManager,
            ArchivesManager archivesManager,
            boolean switchToRemoteFsAsynchronously
    ) {
        this.dirTreeController = dirTreeController;
        this.previewProvider = previewProvider;
        this.localFsManager = localFsManager;
        this.archivesManager = archivesManager;
        this.switchToRemoteFsAsynchronously = switchToRemoteFsAsynchronously;
    }

    @Override
    public void dispose() {
        disposeCurrentFsDataProvider();
    }

    void switchToLocalFs() {
        cancelCurrentSwitch();
        switchFs(localDriveTopDir, localFsManager);
    }

    void switchToRemoteFs(FTPConnectionInfo connectionInfo, FsTypeSwitchProgressHandler handler) {
        cancelCurrentSwitch();
        if (switchToRemoteFsAsynchronously) {
            switchToRemoteFsAsync(connectionInfo, handler);
        } else {
            switchToRemoteFsSync(connectionInfo, handler);
        }
    }

    private void switchToRemoteFsSync(
            FTPConnectionInfo connectionInfo,
            FsTypeSwitchProgressHandler handler
    ) {
        try {
            RemoteFsManager remoteFsManager = makeRemoteFsManager(connectionInfo);
            switchFs(remoteHostTopDir, remoteFsManager);
            handler.onComplete();
        } catch (FTPException e) {
            handler.onFail(e.getMessage());
        }
    }

    private void switchToRemoteFsAsync(
            FTPConnectionInfo connectionInfo,
            FsTypeSwitchProgressHandler handler
    ) {
        asyncRemoteFsSwitcher = new SwingWorker<RemoteFsManager, Void>() {
            @Override
            protected void done() {
                try {
                    RemoteFsManager remoteFsManager = get();
                    switchFs(remoteHostTopDir, remoteFsManager);
                    handler.onComplete();
                } catch (InterruptedException | CancellationException e) {
                    // do nothing
                } catch (ExecutionException e) {
                    handler.onFail(e.getCause().getMessage());
                }
            }

            @Override
            protected RemoteFsManager doInBackground() throws Exception {
                return makeRemoteFsManager(connectionInfo);
            }
        };
        asyncRemoteFsSwitcher.execute();
    }

    void disposeCurrentFsDataProvider() {
        if (asyncFsDataProvider != null) {
            // TODO this `dispose` should be done in background thread
            asyncFsDataProvider.dispose();
            asyncFsDataProvider = null;
        }
    }

    private void cancelCurrentSwitch() {
        if (asyncRemoteFsSwitcher != null) {
            asyncRemoteFsSwitcher.cancel(true);
        }
    }

    private RemoteFsManager makeRemoteFsManager(FTPConnectionInfo connectionInfo)
            throws FTPException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(connectionInfo);
        remoteFsManager.checkConnection();
        return remoteFsManager;
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
