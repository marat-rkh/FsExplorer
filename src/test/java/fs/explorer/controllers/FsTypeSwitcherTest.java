package fs.explorer.controllers;

import fs.explorer.TestEnvironment;
import fs.explorer.providers.dirtree.AsyncFsDataProvider;
import fs.explorer.providers.dirtree.FsManager;
import fs.explorer.providers.dirtree.local.LocalFsManager;
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

    @Before
    public void setUp() {
        dirTreeController = mock(DirTreeController.class);
        previewProvider = mock(DefaultPreviewProvider.class);
        localFsManager = mock(LocalFsManager.class);
        ArchivesManager archivesManager = mock(ArchivesManager.class);
        fsTypeSwitcher = spy(new FsTypeSwitcher(
                dirTreeController,
                previewProvider,
                localFsManager,
                archivesManager,
                false
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
    public void switchesToRemoteFs() {
        Assume.assumeTrue(TestEnvironment.ftpTestsNeeded());

        FsTypeSwitchProgressHandler handler = mock(FsTypeSwitchProgressHandler.class);
        fsTypeSwitcher.switchToRemoteFs(tele2TestServer(), handler);
        verify(fsTypeSwitcher).disposeCurrentFsDataProvider();
        verify(dirTreeController).resetDataProvider(any());
        verify(previewProvider).resetFsManager(any());
        verify(handler).onComplete();
    }

    @Test
    public void doesNotSwitchToRemoteFsOnFsManagerFail() {
        Assume.assumeTrue(TestEnvironment.ftpTestsNeeded());

        FsTypeSwitchProgressHandler handler = mock(FsTypeSwitchProgressHandler.class);
        FTPConnectionInfo noHost = new FTPConnectionInfo("", "", new char[1]);
        fsTypeSwitcher.switchToRemoteFs(noHost, handler);
        verify(fsTypeSwitcher, never()).disposeCurrentFsDataProvider();
        verify(dirTreeController, never()).resetDataProvider(any());
        verify(previewProvider, never()).resetFsManager(any());
        verify(handler).onFail(any());
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

        FsTypeSwitchProgressHandler handler = mock(FsTypeSwitchProgressHandler.class);
        fsTypeSwitcher.switchToRemoteFs(tele2TestServer(), handler);
        fsTypeSwitcher.switchToRemoteFs(rebexTestServer(), handler);
        fsTypeSwitcher.switchToRemoteFs(tele2TestServer(), handler);

        verify(fsTypeSwitcher, times(3)).disposeCurrentFsDataProvider();
        verify(dirTreeController, times(3)).resetDataProvider(any());
        verify(previewProvider, times(3)).resetFsManager(any());
        verify(handler, times(3)).onComplete();
    }

    @Test
    public void switchesToRemoteFsAfterFail() throws FTPException {
        Assume.assumeTrue(TestEnvironment.ftpTestsNeeded());

        FsTypeSwitchProgressHandler failHandler = mock(FsTypeSwitchProgressHandler.class);
        FTPConnectionInfo noHost = new FTPConnectionInfo("", "", new char[1]);
        fsTypeSwitcher.switchToRemoteFs(noHost, failHandler);
        FsTypeSwitchProgressHandler okHandler = mock(FsTypeSwitchProgressHandler.class);
        fsTypeSwitcher.switchToRemoteFs(tele2TestServer(), okHandler);

        verify(failHandler).onFail(any());
        verify(fsTypeSwitcher).disposeCurrentFsDataProvider();
        verify(dirTreeController).resetDataProvider(any());
        verify(previewProvider).resetFsManager(any());
        verify(okHandler).onComplete();
    }

    @Test
    public void switchesToLocalFsAfterFail() throws FTPException {
        Assume.assumeTrue(TestEnvironment.ftpTestsNeeded());

        FsTypeSwitchProgressHandler handler = mock(FsTypeSwitchProgressHandler.class);
        FTPConnectionInfo noHost = new FTPConnectionInfo("", "", new char[1]);
        fsTypeSwitcher.switchToRemoteFs(noHost, handler);
        fsTypeSwitcher.switchToLocalFs();

        verify(handler).onFail(any());
        verify(fsTypeSwitcher).disposeCurrentFsDataProvider();
        verify(dirTreeController).resetDataProvider(any());
        verify(previewProvider).resetFsManager(localFsManager);
    }

    @Test
    public void switchesToRemoteFsAndBack() throws FTPException {
        Assume.assumeTrue(TestEnvironment.ftpTestsNeeded());

        FsTypeSwitchProgressHandler handler = mock(FsTypeSwitchProgressHandler.class);
        fsTypeSwitcher.switchToLocalFs();
        fsTypeSwitcher.switchToRemoteFs(tele2TestServer(), handler);
        fsTypeSwitcher.switchToLocalFs();

        verify(handler).onComplete();
        verify(fsTypeSwitcher, times(3)).disposeCurrentFsDataProvider();

        ArgumentCaptor<AsyncFsDataProvider> captor1 = ArgumentCaptor.forClass(
                AsyncFsDataProvider.class);
        verify(dirTreeController, times(3)).resetDataProvider(captor1.capture());
        assertNotNull(captor1.getAllValues().get(2));

        ArgumentCaptor<FsManager> captor2 = ArgumentCaptor.forClass(FsManager.class);
        verify(previewProvider, times(3)).resetFsManager(captor2.capture());
        assertTrue(captor2.getAllValues().get(2) == localFsManager);
    }

    @Test
    public void switchesToLocalFsAndBack() throws FTPException {
        Assume.assumeTrue(TestEnvironment.ftpTestsNeeded());

        FsTypeSwitchProgressHandler handler = mock(FsTypeSwitchProgressHandler.class);
        fsTypeSwitcher.switchToRemoteFs(tele2TestServer(), handler);
        fsTypeSwitcher.switchToLocalFs();
        fsTypeSwitcher.switchToRemoteFs(tele2TestServer(), handler);

        verify(handler, times(2)).onComplete();
        verify(fsTypeSwitcher, times(3)).disposeCurrentFsDataProvider();

        ArgumentCaptor<AsyncFsDataProvider> captor1 = ArgumentCaptor.forClass(
                AsyncFsDataProvider.class);
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
            FsTypeSwitchProgressHandler handler = mock(FsTypeSwitchProgressHandler.class);
            fsTypeSwitcher.switchToRemoteFs(tele2TestServer(), handler);
            verify(handler).onComplete();
        };
        Runnable toRemoteFailing = () -> {
            FTPConnectionInfo noHost = new FTPConnectionInfo("", "", new char[1]);
            FsTypeSwitchProgressHandler handler = mock(FsTypeSwitchProgressHandler.class);
            fsTypeSwitcher.switchToRemoteFs(noHost, handler);
            verify(handler).onFail(any());
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
        if (lastAction == SwitchAction.Type.TO_REMOTE_FAILING) {
            lastAction = actions.get(4).getType();
        }
        if (lastAction == SwitchAction.Type.TO_LOCAL) {
            ArgumentCaptor<AsyncFsDataProvider> captor1 = ArgumentCaptor.forClass(
                    AsyncFsDataProvider.class);
            verify(dirTreeController, times(5)).resetDataProvider(captor1.capture());
            assertNotNull(captor1.getAllValues().get(2));

            ArgumentCaptor<FsManager> captor2 = ArgumentCaptor.forClass(FsManager.class);
            verify(previewProvider, times(5)).resetFsManager(captor2.capture());
            assertTrue(captor2.getAllValues().get(4) == localFsManager);
        } else if (lastAction == SwitchAction.Type.TO_REMOTE) {
            ArgumentCaptor<AsyncFsDataProvider> captor1 = ArgumentCaptor.forClass(
                    AsyncFsDataProvider.class);
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

        Type getType() {
            return type;
        }

        enum Type {
            TO_LOCAL,
            TO_REMOTE,
            TO_REMOTE_FAILING
        }
    }
}