package fs.explorer.controllers;

import fs.explorer.providers.dirtree.FsPath;
import fs.explorer.providers.dirtree.FsPath.TargetType;
import fs.explorer.providers.dirtree.TreeNodeData;
import fs.explorer.providers.preview.PreviewProvider;
import fs.explorer.views.PreviewPane;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

public class PreviewControllerTest {
    private PreviewPane previewPane;
    private PreviewProvider previewProvider;
    private StatusBarController statusBarController;
    private PreviewController previewController;

    @Before
    public void setUp() {
        previewPane = mock(PreviewPane.class);
        previewProvider = spy(new TestPreviewProvider());
        statusBarController = mock(StatusBarController.class);
        previewController =
                new PreviewController(previewPane, previewProvider, statusBarController);
    }

    @Test
    public void updatesTextFilePreview() {
        previewController.updatePreview(nodeData("/some/dir/file.txt", /*isDir*/false));
        verify(previewProvider).getTextPreview(any(), any(), any());
        verify(statusBarController).setProgressMessage(any());
        verify(previewPane).updatePreview(any());
        verify(statusBarController).setInfoMessage(any(), any());
        verify(statusBarController, never()).setErrorMessage(any(), any());
        verify(statusBarController, never()).setErrorMessage(any());
    }

    @Test
    public void updatesImageFilePreview() {
        previewController.updatePreview(nodeData("/some/dir/file.jpg", /*isDir*/false));
        verify(previewProvider).getImagePreview(any(), any(), any());
        verify(statusBarController).setProgressMessage(any());
        verify(previewPane).updatePreview(any());
        verify(statusBarController).setInfoMessage(any(), any());
        verify(statusBarController, never()).setErrorMessage(any(), any());
        verify(statusBarController, never()).setErrorMessage(any());
    }

    @Test
    public void doesNotUpdatePreviewOnDirectory() {
        previewController.updatePreview(nodeData("/some/dir", /*isDir*/true));
        verify(previewProvider, never()).getTextPreview(any(), any(), any());
        verify(previewProvider, never()).getImagePreview(any(), any(), any());
        verify(previewPane, never()).updatePreview(any());
        verify(statusBarController, never()).setErrorMessage(any(), any());
        verify(statusBarController, never()).setErrorMessage(any());
    }

    @Test
    public void doesNotUpdatePreviewOnUnsupportedFileType() {
        previewController.updatePreview(nodeData("/some/dir/file.psd", /*isDir*/false));
        verify(previewProvider, never()).getTextPreview(any(), any(), any());
        verify(previewProvider, never()).getImagePreview(any(), any(), any());
        verify(previewPane, never()).updatePreview(any());
        verify(statusBarController, never()).setErrorMessage(any(), any());
        verify(statusBarController, never()).setErrorMessage(any());
    }

    @Test
    public void failsToUpdatePreviewOnNullData() {
        previewController.updatePreview(null);
        verify(previewProvider, never()).getTextPreview(any(), any(), any());
        verify(previewProvider, never()).getImagePreview(any(), any(), any());
        verify(previewPane).showDefaultPreview();
        verify(statusBarController).setErrorMessage(any(), any());
    }

    @Test
    public void failsToUpdateTextPreviewOnProviderError() {
        setUpFailingProvider();
        previewController.updatePreview(nodeData("/some/dir/file.txt", /*isDir*/false));
        verify(previewProvider).getTextPreview(any(), any(), any());
        verify(previewPane).showDefaultPreview();
        verify(statusBarController).setErrorMessage(any(), any());
    }

    @Test
    public void failsToUpdateImagePreviewOnProviderError() {
        setUpFailingProvider();
        previewController.updatePreview(nodeData("/some/dir/file.jpg", /*isDir*/false));
        verify(previewProvider).getImagePreview(any(), any(), any());
        verify(previewPane).showDefaultPreview();
        verify(statusBarController).setErrorMessage(any(), any());
    }

    private void setUpFailingProvider() {
        previewProvider = spy(new FailingPreviewProvider());
        previewController =
                new PreviewController(previewPane, previewProvider, statusBarController);
    }

    private static TreeNodeData nodeData(String path, boolean isDir) {
        TargetType targetType = isDir ? TargetType.DIRECTORY : TargetType.FILE;
        return new TreeNodeData("", new FsPath(path, targetType, ""));
    }

    private static class TestPreviewProvider implements PreviewProvider {
        @Override
        public void getTextPreview(
                TreeNodeData data, Consumer<JComponent> onComplete, Consumer<String> onFail) {
            onComplete.accept(null);
        }

        @Override
        public void getImagePreview(
                TreeNodeData data, Consumer<JComponent> onComplete, Consumer<String> onFail) {
            onComplete.accept(null);
        }
    }

    private static class FailingPreviewProvider implements PreviewProvider {
        @Override
        public void getTextPreview(
                TreeNodeData data, Consumer<JComponent> onComplete, Consumer<String> onFail) {
            onFail.accept("");
        }

        @Override
        public void getImagePreview(
                TreeNodeData data, Consumer<JComponent> onComplete, Consumer<String> onFail) {
            onFail.accept("");
        }
    }
}