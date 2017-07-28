package fs.explorer.controllers;

import fs.explorer.controllers.ftpdialog.FTPDialogController;
import fs.explorer.datasource.LocalFilesProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MenuBarControllerTest {
    private MenuBarController controller;
    private DirTreeController dirTreeController;
    private LocalFilesProvider localFilesProvider;
    private FTPDialogController ftpDialogController;

    @Before
    public void setUp() {
        dirTreeController = mock(DirTreeController.class);
        localFilesProvider = mock(LocalFilesProvider.class);
        ftpDialogController = mock(FTPDialogController.class);
        controller = new MenuBarController(
                dirTreeController, localFilesProvider, ftpDialogController);
    }

    @Test
    public void resetsDataProviderOnExploreLocalFilesEvent() throws Exception {
        controller.handleExploreLocalFiles(null);
        ArgumentCaptor<LocalFilesProvider> captor =
                ArgumentCaptor.forClass(LocalFilesProvider.class);
        verify(dirTreeController).resetDataProvider(captor.capture());
        assertTrue(captor.getValue() == localFilesProvider);
    }

    @Test
    public void showsFTPDialogOnExploreRemoteFilesEvent() throws Exception {
        controller.handleExploreRemoteFiles(null);
        verify(ftpDialogController).showAndHandleInput();
    }
}