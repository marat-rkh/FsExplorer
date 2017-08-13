package fs.explorer.controllers;

import fs.explorer.TestEnvironment;
import fs.explorer.providers.dirtree.FsManager;
import fs.explorer.providers.dirtree.local.LocalFsManager;
import fs.explorer.providers.dirtree.TreeDataProvider;
import fs.explorer.providers.dirtree.archives.ArchivesManager;
import fs.explorer.providers.dirtree.remote.FTPConnectionInfo;
import fs.explorer.providers.dirtree.remote.FTPException;
import fs.explorer.providers.preview.DefaultPreviewProvider;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static fs.explorer.providers.dirtree.remote.TestUtils.rebexTestServer;
import static fs.explorer.providers.dirtree.remote.TestUtils.tele2TestServer;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FsTypeSwitcherTest {
    private FsTypeSwitcher fsTypeSwitcher;
    private DirTreeController dirTreeController;
    private DefaultPreviewProvider previewProvider;
    private LocalFsManager localFsManager;
    private ArchivesManager archivesManager;

    @Before
    public void setUp() {
        dirTreeController = mock(DirTreeController.class);
        previewProvider = mock(DefaultPreviewProvider.class);
        localFsManager = mock(LocalFsManager.class);
        archivesManager = mock(ArchivesManager.class);
        fsTypeSwitcher = spy(new FsTypeSwitcher(
                dirTreeController,
                previewProvider,
                localFsManager,
                archivesManager
        ));
    }

    @Test
    public void switchesToLocalFs() {
        fsTypeSwitcher.switchToLocalFs();
        verify(fsTypeSwitcher).disposeCurrentFsDataProvider();
        verify(dirTreeController).resetDataProvider(any());
        verify(previewProvider).resetFsManager(localFsManager);
    }

    @Test
    public void switchesToRemoteFs() throws FTPException {
        Assume.assumeTrue(TestEnvironment.ftpTestsNeeded());

        fsTypeSwitcher.switchToRemoteFs(tele2TestServer());
        verify(fsTypeSwitcher).disposeCurrentFsDataProvider();
        verify(dirTreeController).resetDataProvider(any());
        verify(previewProvider).resetFsManager(any());
    }

    @Test
    public void doesNotSwitchToRemoteFsOnFsManagerFail() throws FTPException {
        Assume.assumeTrue(TestEnvironment.ftpTestsNeeded());

        FTPConnectionInfo noHost = new FTPConnectionInfo("", "", new char[1]);
        try {
            fsTypeSwitcher.switchToRemoteFs(noHost);
            fail();
        } catch (FTPException e) {
            verify(fsTypeSwitcher, never()).disposeCurrentFsDataProvider();
            verify(dirTreeController, never()).resetDataProvider(any());
            verify(previewProvider, never()).resetFsManager(any());
        }
    }

    @Test
    public void switchesToLocalFsMultipleTimes() {
        fsTypeSwitcher.switchToLocalFs();
        fsTypeSwitcher.switchToLocalFs();
        fsTypeSwitcher.switchToLocalFs();
        verify(fsTypeSwitcher, times(3)).disposeCurrentFsDataProvider();
        verify(dirTreeController, times(3)).resetDataProvider(any());
        verify(previewProvider, times(3)).resetFsManager(localFsManager);
    }

    @Test
    public void switchesToRemoteFsMultipleTimes() throws FTPException {
        Assume.assumeTrue(TestEnvironment.ftpTestsNeeded());

        fsTypeSwitcher.switchToRemoteFs(tele2TestServer());
        fsTypeSwitcher.switchToRemoteFs(rebexTestServer());
        fsTypeSwitcher.switchToRemoteFs(tele2TestServer());

        verify(fsTypeSwitcher, times(3)).disposeCurrentFsDataProvider();
        verify(dirTreeController, times(3)).resetDataProvider(any());
        verify(previewProvider, times(3)).resetFsManager(any());
    }

    @Test
    public void switchesToRemoteFsAfterFail() throws FTPException {
        Assume.assumeTrue(TestEnvironment.ftpTestsNeeded());

        FTPConnectionInfo noHost = new FTPConnectionInfo("", "", new char[1]);
        try {
            fsTypeSwitcher.switchToRemoteFs(noHost);
            fail();
        } catch (FTPException e) {
            // do nothing
        }
        fsTypeSwitcher.switchToRemoteFs(tele2TestServer());

        verify(fsTypeSwitcher).disposeCurrentFsDataProvider();
        verify(dirTreeController).resetDataProvider(any());
        verify(previewProvider).resetFsManager(any());
    }

    @Test
    public void switchesToLocalFsAfterFail() throws FTPException {
        Assume.assumeTrue(TestEnvironment.ftpTestsNeeded());

        FTPConnectionInfo noHost = new FTPConnectionInfo("", "", new char[1]);
        try {
            fsTypeSwitcher.switchToRemoteFs(noHost);
            fail();
        } catch (FTPException e) {
            // do nothing
        }
        fsTypeSwitcher.switchToLocalFs();

        verify(fsTypeSwitcher).disposeCurrentFsDataProvider();
        verify(dirTreeController).resetDataProvider(any());
        verify(previewProvider).resetFsManager(localFsManager);
    }

    @Test
    public void switchesToRemoteFsAndBack() throws FTPException {
        Assume.assumeTrue(TestEnvironment.ftpTestsNeeded());

        fsTypeSwitcher.switchToLocalFs();
        fsTypeSwitcher.switchToRemoteFs(tele2TestServer());
        fsTypeSwitcher.switchToLocalFs();

        verify(fsTypeSwitcher, times(3)).disposeCurrentFsDataProvider();

        ArgumentCaptor<TreeDataProvider> captor1 =
                ArgumentCaptor.forClass(TreeDataProvider.class);
        verify(dirTreeController, times(3)).resetDataProvider(captor1.capture());
        assertNotNull(captor1.getAllValues().get(2));

        ArgumentCaptor<FsManager> captor2 = ArgumentCaptor.forClass(FsManager.class);
        verify(previewProvider, times(3)).resetFsManager(captor2.capture());
        assertTrue(captor2.getAllValues().get(2) == localFsManager);
    }

    @Test
    public void switchesToLocalFsAndBack() throws FTPException {
        Assume.assumeTrue(TestEnvironment.ftpTestsNeeded());

        fsTypeSwitcher.switchToRemoteFs(tele2TestServer());
        fsTypeSwitcher.switchToLocalFs();
        fsTypeSwitcher.switchToRemoteFs(tele2TestServer());

        verify(fsTypeSwitcher, times(3)).disposeCurrentFsDataProvider();

        ArgumentCaptor<TreeDataProvider> captor1 =
                ArgumentCaptor.forClass(TreeDataProvider.class);
        verify(dirTreeController, times(3)).resetDataProvider(captor1.capture());
        assertNotNull(captor1.getAllValues().get(2));

        ArgumentCaptor<FsManager> captor2 = ArgumentCaptor.forClass(FsManager.class);
        verify(previewProvider, times(3)).resetFsManager(captor2.capture());
        assertTrue(captor2.getAllValues().get(2) != localFsManager);
    }

    @Test
    public void switchesMultipleTimes() throws FTPException {
        Assume.assumeTrue(TestEnvironment.ftpTestsNeeded());

        Runnable toLocal = () -> fsTypeSwitcher.switchToLocalFs();
        Runnable toRemote = () -> {
            try {
                fsTypeSwitcher.switchToRemoteFs(tele2TestServer());
            } catch (FTPException e) {
                fail();
            }
        };
        Runnable toRemoteFailing = () -> {
            try {
                FTPConnectionInfo noHost = new FTPConnectionInfo("", "", new char[1]);
                fsTypeSwitcher.switchToRemoteFs(noHost);
                fail();
            } catch (FTPException e) {
                // thrown as expected
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
            assertNotNull(captor1.getAllValues().get(2));

            ArgumentCaptor<FsManager> captor2 = ArgumentCaptor.forClass(FsManager.class);
            verify(previewProvider, times(5)).resetFsManager(captor2.capture());
            assertTrue(captor2.getAllValues().get(4) == localFsManager);
        } else if(lastAction == SwitchAction.Type.TO_REMOTE) {
            ArgumentCaptor<TreeDataProvider> captor1 =
                    ArgumentCaptor.forClass(TreeDataProvider.class);
            verify(dirTreeController, times(5)).resetDataProvider(captor1.capture());
            assertNotNull(captor1.getAllValues().get(2));

            ArgumentCaptor<FsManager> captor2 = ArgumentCaptor.forClass(FsManager.class);
            verify(previewProvider, times(5)).resetFsManager(captor2.capture());
            assertTrue(captor2.getAllValues().get(4) != localFsManager);
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

        Type getType() { return type; }

        enum Type {
            TO_LOCAL,
            TO_REMOTE,
            TO_REMOTE_FAILING
        }
    }
}