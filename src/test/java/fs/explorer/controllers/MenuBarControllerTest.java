package fs.explorer.controllers;

import fs.explorer.controllers.ftpdialog.FTPDialogController;
import fs.explorer.providers.FsDataProvider;
import fs.explorer.providers.FsManager;
import fs.explorer.providers.preview.DefaultPreviewProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MenuBarControllerTest {
    private MenuBarController controller;
    private DirTreeController dirTreeController;
    private DefaultPreviewProvider previewProvider;
    private FsManager localFsManager;
    private FsDataProvider fsDataProvider;
    private FTPDialogController ftpDialogController;

    @Before
    public void setUp() {
        dirTreeController = mock(DirTreeController.class);
        previewProvider = mock(DefaultPreviewProvider.class);
        localFsManager = mock(FsManager.class);
        fsDataProvider = mock(FsDataProvider.class);
        ftpDialogController = mock(FTPDialogController.class);
        controller = new MenuBarController(
                dirTreeController, previewProvider, localFsManager, fsDataProvider, ftpDialogController);
    }

    @Test
    public void resetsDataProviderOnExploreLocalFilesEvent() throws Exception {
        controller.handleExploreLocalFiles(null);
        ArgumentCaptor<FsDataProvider> captor =
                ArgumentCaptor.forClass(FsDataProvider.class);
        verify(dirTreeController).resetDataProvider(captor.capture());
        assertTrue(captor.getValue() == fsDataProvider);
    }

    @Test
    public void showsFTPDialogOnExploreRemoteFilesEvent() throws Exception {
        controller.handleExploreRemoteFiles(null);
        verify(ftpDialogController).showAndHandleInput();
    }
}