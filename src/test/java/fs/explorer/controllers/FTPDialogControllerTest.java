package fs.explorer.controllers;

import fs.explorer.providers.dirtree.remote.FTPConnectionInfo;
import fs.explorer.providers.dirtree.remote.FTPException;
import fs.explorer.views.FTPDialog;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FTPDialogControllerTest {
    private FTPDialogController ftpDialogController;
    private FTPDialog ftpDialog;
    private FTPInfoValidator ftpInfoValidator;
    private FsTypeSwitcher fsTypeSwitcher;
    private StatusBarController statusBarController;

    @Before
    public void setUp() {
        ftpDialog = mock(FTPDialog.class);
        ftpInfoValidator = mock(FTPInfoValidator.class);
        fsTypeSwitcher = mock(FsTypeSwitcher.class);
        statusBarController = mock(StatusBarController.class);
        ftpDialogController = new FTPDialogController(
                ftpDialog, ftpInfoValidator, fsTypeSwitcher, statusBarController);
    }

    @Test
    public void showsFTPDialogWithEmptyError() {
        when(ftpDialog.showAndWaitResult()).thenReturn(Optional.empty());
        ftpDialogController.showAndHandleInput();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ftpDialog).setErrorMessage(captor.capture());
        assertTrue(captor.getValue().isEmpty());
    }

    @Test
    public void doesNothingOnEmptyFTPConnectionInfo() throws FTPException {
        when(ftpDialog.showAndWaitResult()).thenReturn(Optional.empty());
        ftpDialogController.showAndHandleInput();

        verify(ftpInfoValidator, never()).validate(any());
        verify(fsTypeSwitcher, never()).switchToLocalFs();
        verify(fsTypeSwitcher, never()).switchToRemoteFs(any());
        verify(statusBarController, never()).setInfoMessage(any(), any());
        verify(statusBarController, never()).setErrorMessage(any(), any());
    }

    @Test
    public void showsFTPDialogAgainOnValidationFail() {
        FTPConnectionInfo testInfo =
                new FTPConnectionInfo("host", "user", new char[]{'p', 'a', 's', 's'});
        when(ftpDialog.showAndWaitResult())
                .thenReturn(Optional.of(testInfo))
                .thenReturn(Optional.empty());
        when(ftpInfoValidator.validate(any())).thenReturn(Optional.of("Some error"));
        ftpDialogController.showAndHandleInput();

        verify(ftpDialog, times(2)).showAndWaitResult();
        verify(ftpInfoValidator).validate(testInfo);
        // last call with error message
        verify(ftpDialog).setErrorMessage("Some error");
    }

    @Test
    public void switchesToRemoteFs() throws FTPException {
        FTPConnectionInfo testInfo =
                new FTPConnectionInfo("host", "user", new char[]{'p', 'a', 's', 's'});
        when(ftpDialog.showAndWaitResult()).thenReturn(Optional.of(testInfo));
        when(ftpInfoValidator.validate(any())).thenReturn(Optional.empty());
        ftpDialogController.showAndHandleInput();

        verify(fsTypeSwitcher).switchToRemoteFs(testInfo);
        verify(statusBarController).setInfoMessage(any(), any());
    }

    @Test
    public void switchesToRemoteFsAfterDialogShownAgain() throws FTPException {
        FTPConnectionInfo testInfo =
                new FTPConnectionInfo("host", "user", new char[]{'p', 'a', 's', 's'});
        when(ftpDialog.showAndWaitResult()).thenReturn(Optional.of(testInfo));
        when(ftpInfoValidator.validate(any()))
                .thenReturn(Optional.of("Some error"))
                .thenReturn(Optional.empty());
        ftpDialogController.showAndHandleInput();

        verify(fsTypeSwitcher).switchToRemoteFs(testInfo);
        verify(statusBarController).setInfoMessage(any(), any());
    }

    @Test
    public void handlesFsSwitchFailure() throws FTPException {
        FTPConnectionInfo testInfo =
                new FTPConnectionInfo("host", "user", new char[]{'p', 'a', 's', 's'});
        when(ftpDialog.showAndWaitResult()).thenReturn(Optional.of(testInfo));
        when(ftpInfoValidator.validate(any())).thenReturn(Optional.empty());
        doThrow(FTPException.class).when(fsTypeSwitcher).switchToRemoteFs(any());
        ftpDialogController.showAndHandleInput();

        verify(statusBarController, never()).setInfoMessage(any(), any());
        verify(statusBarController).setErrorMessage(any(), any());
    }

    @Test
    public void showsAndHandlesInputOnHandleLastInputWithNoLastConnection() throws FTPException {
        FTPConnectionInfo testInfo =
                new FTPConnectionInfo("host", "user", new char[]{'p', 'a', 's', 's'});
        when(ftpDialog.showAndWaitResult()).thenReturn(Optional.of(testInfo));
        when(ftpInfoValidator.validate(any())).thenReturn(Optional.empty());
        ftpDialogController.handleLastInput();

        verify(fsTypeSwitcher).switchToRemoteFs(testInfo);
        verify(statusBarController).setInfoMessage(any(), any());
    }

    @Test
    public void handlesLastInputWhenLastConnectionPresent() throws FTPException {
        FTPConnectionInfo testInfo =
                new FTPConnectionInfo("host", "user", new char[]{'p', 'a', 's', 's'});
        when(ftpDialog.showAndWaitResult()).thenReturn(Optional.of(testInfo));
        when(ftpInfoValidator.validate(any())).thenReturn(Optional.empty());
        ftpDialogController.showAndHandleInput();
        ftpDialogController.handleLastInput();

        verify(fsTypeSwitcher, times(2)).switchToRemoteFs(testInfo);
        verify(statusBarController, times(2)).setInfoMessage(any(), any());
    }
}