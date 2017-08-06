package fs.explorer.controllers;

import fs.explorer.providers.dirtree.FsManager;
import fs.explorer.providers.dirtree.local.LocalFsManager;
import fs.explorer.providers.dirtree.TreeDataProvider;
import fs.explorer.providers.dirtree.archives.ArchivesManager;
import fs.explorer.providers.dirtree.remote.FTPConnectionInfo;
import fs.explorer.providers.dirtree.remote.FTPException;
import fs.explorer.providers.dirtree.remote.RemoteFsManager;
import fs.explorer.providers.preview.DefaultPreviewProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FsTypeSwitcherTest {
    private FsTypeSwitcher fsTypeSwitcher;
    private DirTreeController dirTreeController;
    private DefaultPreviewProvider previewProvider;
    private TreeDataProvider localFsDataProvider;
    private LocalFsManager localFsManager;
    private RemoteFsManager remoteFsManager;
    private ArchivesManager archivesManager;

    @Before
    public void setUp() {
        dirTreeController = mock(DirTreeController.class);
        previewProvider = mock(DefaultPreviewProvider.class);
        localFsDataProvider = mock(TreeDataProvider.class);
        localFsManager = mock(LocalFsManager.class);
        remoteFsManager = mock(RemoteFsManager.class);
        // TODO test manipulations with archivesManager
        archivesManager = mock(ArchivesManager.class);
        fsTypeSwitcher = spy(new FsTypeSwitcher(
                dirTreeController,
                previewProvider,
                localFsDataProvider,
                localFsManager,
                remoteFsManager,
                archivesManager));
    }

    @Test
    public void switchesToLocalFs() {
        fsTypeSwitcher.switchToLocalFs();
        verify(fsTypeSwitcher).disposeCurrentRemoteFsDataProvider();
        verify(dirTreeController).resetDataProvider(localFsDataProvider);
        verify(previewProvider).resetFsManager(localFsManager);
    }

    @Test
    public void switchesToRemoteFs() throws FTPException {
        FTPConnectionInfo connectionInfo = mock(FTPConnectionInfo.class);
        fsTypeSwitcher.switchToRemoteFs(connectionInfo);
        verify(remoteFsManager).reconnect(connectionInfo);
        verify(fsTypeSwitcher).disposeCurrentRemoteFsDataProvider();
        verify(dirTreeController).resetDataProvider(any());
        verify(previewProvider).resetFsManager(remoteFsManager);
    }

    @Test
    public void doesNotSwitchToRemoteFsOnFsManagerFail() throws FTPException {
        doThrow(FTPException.class).when(remoteFsManager).reconnect(any());
        FTPConnectionInfo connectionInfo = mock(FTPConnectionInfo.class);
        try {
            fsTypeSwitcher.switchToRemoteFs(connectionInfo);
            fail();
        } catch (FTPException e) {
            verify(fsTypeSwitcher, never()).disposeCurrentRemoteFsDataProvider();
            verify(dirTreeController, never()).resetDataProvider(any());
            verify(previewProvider, never()).resetFsManager(any());
        }
    }

    @Test
    public void switchesToLocalFsMultipleTimes() {
        fsTypeSwitcher.switchToLocalFs();
        fsTypeSwitcher.switchToLocalFs();
        fsTypeSwitcher.switchToLocalFs();
        verify(fsTypeSwitcher, times(3)).disposeCurrentRemoteFsDataProvider();
        verify(dirTreeController, times(3)).resetDataProvider(localFsDataProvider);
        verify(previewProvider, times(3)).resetFsManager(localFsManager);
    }

    @Test
    public void switchesToRemoteFsMultipleTimes() throws FTPException {
        FTPConnectionInfo connectionInfo = mock(FTPConnectionInfo.class);
        fsTypeSwitcher.switchToRemoteFs(connectionInfo);
        fsTypeSwitcher.switchToRemoteFs(connectionInfo);
        fsTypeSwitcher.switchToRemoteFs(connectionInfo);

        verify(remoteFsManager, times(3)).reconnect(connectionInfo);
        verify(fsTypeSwitcher, times(3)).disposeCurrentRemoteFsDataProvider();
        verify(dirTreeController, times(3)).resetDataProvider(any());
        verify(previewProvider, times(3)).resetFsManager(remoteFsManager);
    }

    @Test
    public void switchesToRemoteFsAfterFail() throws FTPException {
        doThrow(FTPException.class).when(remoteFsManager).reconnect(any());
        FTPConnectionInfo connectionInfo = mock(FTPConnectionInfo.class);
        try {
            fsTypeSwitcher.switchToRemoteFs(connectionInfo);
            fail();
        } catch (FTPException e) {}
        doNothing().when(remoteFsManager).reconnect(any());
        fsTypeSwitcher.switchToRemoteFs(connectionInfo);

        verify(remoteFsManager, times(2)).reconnect(connectionInfo);
        verify(fsTypeSwitcher).disposeCurrentRemoteFsDataProvider();
        verify(dirTreeController).resetDataProvider(any());
        verify(previewProvider).resetFsManager(remoteFsManager);
    }

    @Test
    public void switchesToLocalFsAfterFail() throws FTPException {
        doThrow(FTPException.class).when(remoteFsManager).reconnect(any());
        FTPConnectionInfo connectionInfo = mock(FTPConnectionInfo.class);
        try {
            fsTypeSwitcher.switchToRemoteFs(connectionInfo);
            fail();
        } catch (FTPException e) {}
        doNothing().when(remoteFsManager).reconnect(any());
        fsTypeSwitcher.switchToLocalFs();

        verify(remoteFsManager).reconnect(connectionInfo);
        verify(fsTypeSwitcher).disposeCurrentRemoteFsDataProvider();
        verify(dirTreeController).resetDataProvider(localFsDataProvider);
        verify(previewProvider).resetFsManager(localFsManager);
    }

    @Test
    public void switchesToRemoteFsAndBack() throws FTPException {
        fsTypeSwitcher.switchToLocalFs();
        FTPConnectionInfo connectionInfo = mock(FTPConnectionInfo.class);
        fsTypeSwitcher.switchToRemoteFs(connectionInfo);
        fsTypeSwitcher.switchToLocalFs();

        verify(fsTypeSwitcher, times(3)).disposeCurrentRemoteFsDataProvider();

        ArgumentCaptor<TreeDataProvider> captor1 =
                ArgumentCaptor.forClass(TreeDataProvider.class);
        verify(dirTreeController, times(3)).resetDataProvider(captor1.capture());
        assertTrue(captor1.getAllValues().get(2) == localFsDataProvider);

        ArgumentCaptor<FsManager> captor2 = ArgumentCaptor.forClass(FsManager.class);
        verify(previewProvider, times(3)).resetFsManager(captor2.capture());
        assertTrue(captor2.getAllValues().get(2) == localFsManager);
    }

    @Test
    public void switchesToLocalFsAndBack() throws FTPException {
        FTPConnectionInfo connectionInfo = mock(FTPConnectionInfo.class);
        fsTypeSwitcher.switchToRemoteFs(connectionInfo);
        fsTypeSwitcher.switchToLocalFs();
        fsTypeSwitcher.switchToRemoteFs(connectionInfo);

        verify(fsTypeSwitcher, times(3)).disposeCurrentRemoteFsDataProvider();

        ArgumentCaptor<TreeDataProvider> captor1 =
                ArgumentCaptor.forClass(TreeDataProvider.class);
        verify(dirTreeController, times(3)).resetDataProvider(captor1.capture());
        assertTrue(captor1.getAllValues().get(2) != localFsDataProvider);

        ArgumentCaptor<FsManager> captor2 = ArgumentCaptor.forClass(FsManager.class);
        verify(previewProvider, times(3)).resetFsManager(captor2.capture());
        assertTrue(captor2.getAllValues().get(2) == remoteFsManager);
    }

    @Test
    public void switchesMultipleTimes() throws FTPException {
        Runnable toLocal = () -> fsTypeSwitcher.switchToLocalFs();
        Runnable toRemote = () -> {
            try {
                fsTypeSwitcher.switchToRemoteFs(mock(FTPConnectionInfo.class));
            } catch (FTPException e) {
                fail();
            }
        };
        Runnable toRemoteFailing = () -> {
            try {
                doThrow(FTPException.class).when(remoteFsManager).reconnect(any());
                fsTypeSwitcher.switchToRemoteFs(mock(FTPConnectionInfo.class));
                fail();
            } catch (FTPException e) {
                // thrown as expected
            } finally {
                try {
                    doNothing().when(remoteFsManager).reconnect(any());
                } catch (FTPException e) {
                    fail();
                }
            }
        };
        List<SwitchAction> actions = Arrays.asList(
                new SwitchAction(toLocal, SwitchAction.Type.TO_LOCAL),
                new SwitchAction(toLocal, SwitchAction.Type.TO_LOCAL),
                new SwitchAction(toLocal, SwitchAction.Type.TO_LOCAL),
                new SwitchAction(toRemote, SwitchAction.Type.TO_REMOTE),
                new SwitchAction(toRemote, SwitchAction.Type.TO_REMOTE),
                new SwitchAction(toRemoteFailing, SwitchAction.Type.TO_REMOTE_FAILING)
        );
        Collections.shuffle(actions);
        actions.forEach(Runnable::run);
        SwitchAction.Type lastAction = actions.get(5).getType();
        if(lastAction == SwitchAction.Type.TO_REMOTE_FAILING) {
            lastAction = actions.get(4).getType();
        }
        if(lastAction == SwitchAction.Type.TO_LOCAL) {
            ArgumentCaptor<TreeDataProvider> captor1 =
                    ArgumentCaptor.forClass(TreeDataProvider.class);
            verify(dirTreeController, times(5)).resetDataProvider(captor1.capture());
            assertTrue(captor1.getAllValues().get(4) == localFsDataProvider);

            ArgumentCaptor<FsManager> captor2 = ArgumentCaptor.forClass(FsManager.class);
            verify(previewProvider, times(5)).resetFsManager(captor2.capture());
            assertTrue(captor2.getAllValues().get(4) == localFsManager);
        } else if(lastAction == SwitchAction.Type.TO_REMOTE) {
            ArgumentCaptor<TreeDataProvider> captor1 =
                    ArgumentCaptor.forClass(TreeDataProvider.class);
            verify(dirTreeController, times(5)).resetDataProvider(captor1.capture());
            assertTrue(captor1.getAllValues().get(4) != localFsDataProvider);

            ArgumentCaptor<FsManager> captor2 = ArgumentCaptor.forClass(FsManager.class);
            verify(previewProvider, times(5)).resetFsManager(captor2.capture());
            assertTrue(captor2.getAllValues().get(4) == remoteFsManager);
        } else {
            fail("unexpected action type, check your test correctness");
        }
    }

    private static class SwitchAction implements Runnable {
        private final Runnable runnable;
        private final Type type;

        private SwitchAction(Runnable runnable, Type type) {
            this.runnable = runnable;
            this.type = type;
        }

        @Override
        public void run() {
            runnable.run();
        }

        public Type getType() { return type; }

        enum Type {
            TO_LOCAL,
            TO_REMOTE,
            TO_REMOTE_FAILING
        }
    }
}