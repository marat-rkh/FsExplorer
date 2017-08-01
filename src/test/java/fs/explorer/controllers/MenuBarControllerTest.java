package fs.explorer.controllers;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MenuBarControllerTest {
    private MenuBarController controller;
    private FsTypeSwitcher fsTypeSwitcher;
    private FTPDialogController ftpDialogController;

    @Before
    public void setUp() {
        fsTypeSwitcher = mock(FsTypeSwitcher.class);
        ftpDialogController = mock(FTPDialogController.class);
        controller = new MenuBarController(fsTypeSwitcher, ftpDialogController);
    }

    @Test
    public void resetsDataProviderOnExploreLocalFilesEvent() throws Exception {
        controller.handleExploreLocalFiles(null);
        verify(fsTypeSwitcher).switchToLocalFs();
    }

    @Test
    public void showsFTPDialogOnExploreRemoteFilesEvent() throws Exception {
        controller.handleExploreRemoteFiles(null);
        verify(ftpDialogController).showAndHandleInput();
    }
}