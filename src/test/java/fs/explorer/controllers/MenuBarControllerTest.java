package fs.explorer.controllers;

import fs.explorer.datasource.LocalFilesProvider;
import fs.explorer.models.dirtree.DirTreeModel;
import fs.explorer.models.ftpdialog.FTPDialogModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MenuBarControllerTest {
    private MenuBarController controller;
    private DirTreeModel dirTreeModel;
    private LocalFilesProvider localFilesProvider;
    private FTPDialogModel ftpDialogModel;

    @Before
    public void setUp() {
        dirTreeModel = mock(DirTreeModel.class);
        localFilesProvider = mock(LocalFilesProvider.class);
        ftpDialogModel = mock(FTPDialogModel.class);
        controller = new MenuBarController(dirTreeModel, localFilesProvider, ftpDialogModel);
    }

    @Test
    public void resetsDataProviderOnExploreLocalFilesEvent() throws Exception {
        controller.handleExploreLocalFiles(null);
        ArgumentCaptor<LocalFilesProvider> captor =
                ArgumentCaptor.forClass(LocalFilesProvider.class);
        verify(dirTreeModel).resetDataProvider(captor.capture());
        assertTrue(captor.getValue() == localFilesProvider);
    }

    @Test
    public void showsFTPDialogOnExploreRemoteFilesEvent() throws Exception {
        controller.handleExploreRemoteFiles(null);
        verify(ftpDialogModel).show();
    }
}