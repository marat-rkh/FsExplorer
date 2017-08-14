package fs.explorer.controllers;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ToolBarControllerTest {
    private ToolBarController controller;
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
        controller = new ToolBarController(
                fsTypeSwitcher, ftpDialogController, dirTreeController, statusBarController);
    }

    @Test
    public void resetsDataProviderOnExploreLocalFilesEvent() throws Exception {
        controller.handleExploreLocalFiles(null);
        verify(fsTypeSwitcher).switchToLocalFs();
    }

    @Test
    public void writesMessageToStatusBarOnExploreLocalFilesEvent() throws Exception {
        controller.handleExploreLocalFiles(null);
        verify(statusBarController).setInfoMessage(any());
    }

    @Test
    public void showsFTPDialogOnExploreRemoteFilesEvent() throws Exception {
        controller.handleExploreRemoteFiles(null);
        verify(ftpDialogController).showAndHandleInput();
    }

    @Test
    public void handlesReconnectToLastRemoteHost() throws Exception {
        controller.handleReconnectToLastRemoteHost(null);
        verify(ftpDialogController).handleLastInput();
    }

    @Test
    public void reloadsLastSelectedNodeOnSelectedReloadEvent() throws Exception {
        controller.handleSelectedReload(null);
        verify(dirTreeController).reloadLastSelectedNode();
    }
}