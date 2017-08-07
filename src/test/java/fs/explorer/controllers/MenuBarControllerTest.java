package fs.explorer.controllers;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MenuBarControllerTest {
    private MenuBarController controller;
    private FsTypeSwitcher fsTypeSwitcher;
    private FTPDialogController ftpDialogController;
    private DirTreeController dirTreeController;
    private StatusBarController statusBarController;

    @Before
    public void setUp() {
        fsTypeSwitcher = mock(FsTypeSwitcher.class);
        ftpDialogController = mock(FTPDialogController.class);
        dirTreeController = mock(DirTreeController.class);
        statusBarController = mock(StatusBarController.class);
        controller = new MenuBarController(
                fsTypeSwitcher, ftpDialogController, dirTreeController, statusBarController);
    }

    @Test
    public void resetsDataProviderOnExploreLocalFilesEvent() throws Exception {
        controller.handleExploreLocalFiles(null);
        verify(fsTypeSwitcher).switchToLocalFs();
    }

    @Test
    public void clearsStatusBarOnExploreLocalFilesEvent() throws Exception {
        controller.handleExploreLocalFiles(null);
        verify(statusBarController).clear();
    }

    @Test
    public void showsFTPDialogOnExploreRemoteFilesEvent() throws Exception {
        controller.handleExploreRemoteFiles(null);
        verify(ftpDialogController).showAndHandleInput();
    }

    @Test
    public void reloadsLastSelectedNodeOnSelectedReloadEvent() throws Exception {
        controller.handleSelectedReload(null);
        verify(dirTreeController).reloadLastSelectedNode();
    }
}