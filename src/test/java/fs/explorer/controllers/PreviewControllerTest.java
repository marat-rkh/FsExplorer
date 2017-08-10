package fs.explorer.controllers;

import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.TreeNodeData;
import fs.explorer.providers.dirtree.path.TargetType;
import fs.explorer.providers.preview.*;
import fs.explorer.views.PreviewPane;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class PreviewControllerTest {
    private PreviewPane previewPane;
    private PreviewProvider previewProvider;
    private StatusBarController statusBarController;
    private PreviewController previewController;

    @Before
    public void setUp() {
        previewPane = mock(PreviewPane.class);
        previewProvider = spy(new CompletingPreviewProvider());
        statusBarController = mock(StatusBarController.class);
        previewController =
                new PreviewController(previewPane, previewProvider, statusBarController);
    }

    @Test
    public void updatesFilePreview() {
        TreeNodeData testData = nodeData("/some/dir/file.txt", false, "file.txt");
        previewController.updatePreview(testData);
        verify(statusBarController).setProgressMessage(any());
        verify(previewProvider).getPreview(any(), any());
        verify(previewPane).updatePreview(any());
        verify(statusBarController).setInfoMessage(any(), any());
        verify(statusBarController, never()).setErrorMessage(any(), any());
        verify(statusBarController, never()).setErrorMessage(any());
    }

    @Test
    public void doesNotUpdatePreviewOnDirectory() {
        previewController.updatePreview(nodeData("/some/dir", true, "dir"));
        verify(previewProvider, never()).getPreview(any(), any());
        verify(previewPane, never()).updatePreview(any());
        verify(statusBarController, never()).setErrorMessage(any(), any());
        verify(statusBarController, never()).setErrorMessage(any());
    }

    @Test
    public void handlesPreviewUpdateWhenProviderCanNotRender() {
        setUpCanNotRenderPreviewProvider();
        TreeNodeData testData = nodeData("/some/dir/file.psd", false, "file.psd");
        previewController.updatePreview(testData);
        verify(statusBarController).setProgressMessage(any());
        verify(previewProvider).getPreview(any(), any());
        verify(previewPane).showDefaultPreview();
        verify(statusBarController).clear();
    }

    @Test
    public void failsToUpdatePreviewOnNullData() {
        previewController.updatePreview(null);
        verify(previewProvider, never()).getPreview(any(), any());
        verify(previewPane).showDefaultPreview();
        verify(statusBarController).setErrorMessage(any(), any());
    }

    @Test
    public void failsToUpdateTextPreviewOnProviderError() {
        setUpFailingProvider();
        TreeNodeData testData = nodeData("/some/dir/file.txt", false, "file.txt");
        previewController.updatePreview(testData);
        verify(statusBarController).setProgressMessage(any());
        verify(previewProvider).getPreview(any(), any());
        verify(previewPane).showDefaultPreview();
        verify(statusBarController).setErrorMessage(any(), any());
    }

    private void setUpFailingProvider() {
        previewProvider = spy(new FailingPreviewProvider());
        previewController =
                new PreviewController(previewPane, previewProvider, statusBarController);
    }

    private void setUpCanNotRenderPreviewProvider() {
        previewProvider = spy(new CanNotRenderPreviewProvider());
        previewController =
                new PreviewController(previewPane, previewProvider, statusBarController);
    }

    private static TreeNodeData nodeData(String path, boolean isDir, String lastComponent) {
        TargetType targetType = isDir ? TargetType.DIRECTORY : TargetType.FILE;
        return new TreeNodeData("", new FsPath(path, targetType, lastComponent));
    }

    private static class CompletingPreviewProvider implements PreviewProvider {
        @Override
        public void getPreview(TreeNodeData data, PreviewProgressHandler progressHandler) {
            progressHandler.onComplete(null);
        }
    }

    private static class FailingPreviewProvider implements PreviewProvider {
        @Override
        public void getPreview(TreeNodeData data, PreviewProgressHandler progressHandler) {
            progressHandler.onError("");
        }
    }

    private static class CanNotRenderPreviewProvider implements PreviewProvider {
        @Override
        public void getPreview(TreeNodeData data, PreviewProgressHandler progressHandler) {
            progressHandler.onCanNotRenderer();
        }
    }
}