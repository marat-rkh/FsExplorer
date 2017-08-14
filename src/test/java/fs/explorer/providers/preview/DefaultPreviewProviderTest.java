package fs.explorer.providers.preview;

import fs.explorer.providers.dirtree.FsManager;
import fs.explorer.providers.dirtree.TreeNodeData;
import fs.explorer.providers.dirtree.archives.ArchivesManager;
import fs.explorer.providers.dirtree.path.ArchiveEntryPath;
import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.path.TargetType;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class DefaultPreviewProviderTest {
    private FsManager fsManager;
    private ArchivesManager archivesManager;
    private List<PreviewRenderer> previewRenderers;
    private DefaultPreviewProvider previewProvider;

    @Before
    public void setUp() throws IOException {
        fsManager = mock(FsManager.class);
        archivesManager = mock(ArchivesManager.class);
        when(fsManager.readFile(any())).thenReturn(new byte[1]);
        when(archivesManager.readEntry(any(), any())).thenReturn(new byte[1]);
        previewRenderers = new ArrayList<>();
        previewProvider = new DefaultPreviewProvider(fsManager, archivesManager, previewRenderers);
    }

    @Test
    public void doesNotProvidePreviewOnNullData() {
        PreviewProgressHandler progressHandler = mock(PreviewProgressHandler.class);
        previewProvider.getPreview(null, null, progressHandler);

        verify(progressHandler).onError(any());
        verify(progressHandler, never()).onComplete(any());
        verify(progressHandler, never()).onCanNotRenderer();
    }

    @Test
    public void canNotRenderOnDirectory() throws IOException {
        TreeNodeData dirNode = new TreeNodeData(
                "", new FsPath("some/entry", TargetType.DIRECTORY, ""));
        PreviewProgressHandler progressHandler = mock(PreviewProgressHandler.class);
        previewProvider.getPreview(dirNode, null, progressHandler);

        verify(progressHandler, never()).onError(any());
        verify(progressHandler, never()).onComplete(any());
        verify(progressHandler).onCanNotRenderer();
    }

    @Test
    public void canNotRenderOnNullRenderers() throws IOException {
        setUpNullRenderers();
        PreviewProgressHandler progressHandler = mock(PreviewProgressHandler.class);
        previewProvider.getPreview(fsPathNode(""), null, progressHandler);

        verify(progressHandler, never()).onError(any());
        verify(progressHandler, never()).onComplete(any());
        verify(progressHandler).onCanNotRenderer();
    }

    @Test
    public void canNotRenderOnEmptyRenderers() throws IOException {
        PreviewProgressHandler progressHandler = mock(PreviewProgressHandler.class);
        previewProvider.getPreview(fsPathNode(""), null, progressHandler);

        verify(progressHandler, never()).onError(any());
        verify(progressHandler, never()).onComplete(any());
        verify(progressHandler).onCanNotRenderer();
    }

    @Test
    public void doesNotProvidePreviewOnNullFromFsManager() throws IOException {
        setUpTestRenderers();
        when(fsManager.readFile(any())).thenReturn(null);
        PreviewProgressHandler progressHandler = mock(PreviewProgressHandler.class);
        previewProvider.getPreview(fsPathNode("file.txt"), null, progressHandler);

        verify(progressHandler).onError(any());
        verify(progressHandler, never()).onComplete(any());
        verify(progressHandler, never()).onCanNotRenderer();
    }

    @Test
    public void doesNotProvidePreviewOnNullFromArchivesManager() throws IOException {
        setUpTestRenderers();
        when(archivesManager.readEntry(any(), any())).thenReturn(null);
        PreviewProgressHandler progressHandler = mock(PreviewProgressHandler.class);
        previewProvider.getPreview(archiveEntryNode("file.txt"), null, progressHandler);

        verify(progressHandler).onError(any());
        verify(progressHandler, never()).onComplete(any());
        verify(progressHandler, never()).onCanNotRenderer();
    }

    @Test
    public void doesNotProvidePreviewOnFsManagerFail() throws IOException {
        setUpTestRenderers();
        when(fsManager.readFile(any())).thenThrow(new IOException());
        PreviewProgressHandler progressHandler = mock(PreviewProgressHandler.class);
        previewProvider.getPreview(fsPathNode("file.txt"), null, progressHandler);

        verify(progressHandler).onError(any());
        verify(progressHandler, never()).onComplete(any());
        verify(progressHandler, never()).onCanNotRenderer();
    }

    @Test
    public void doesNotProvidePreviewOnArchivesManagerFail() throws IOException {
        setUpTestRenderers();
        when(archivesManager.readEntry(any(), any())).thenThrow(new IOException());
        PreviewProgressHandler progressHandler = mock(PreviewProgressHandler.class);
        previewProvider.getPreview(archiveEntryNode("file.txt"), null, progressHandler);

        verify(progressHandler).onError(any());
        verify(progressHandler, never()).onComplete(any());
        verify(progressHandler, never()).onCanNotRenderer();
    }

    @Test
    public void canNotRenderWhenNoAppropriateRenderer() throws IOException {
        setUpTestRenderers();
        PreviewProgressHandler progressHandler = mock(PreviewProgressHandler.class);
        previewProvider.getPreview(fsPathNode("file.psd"), null, progressHandler);

        verify(progressHandler, never()).onError(any());
        verify(progressHandler, never()).onComplete(any());
        verify(progressHandler).onCanNotRenderer();
    }

    @Test
    public void doesNotProvidePreviewOnRenderingFail() throws IOException {
        setUpFailingRenderers();
        PreviewProgressHandler progressHandler = mock(PreviewProgressHandler.class);
        previewProvider.getPreview(fsPathNode("file.txt"), null, progressHandler);

        verify(progressHandler).onError(any());
        verify(progressHandler, never()).onComplete(any());
        verify(progressHandler, never()).onCanNotRenderer();
    }

    @Test
    public void doesNothingOnRenderingInterrupted() throws IOException {
        setUpFailingRenderers();
        PreviewProgressHandler progressHandler = mock(PreviewProgressHandler.class);
        previewProvider.getPreview(fsPathNode("file.jpg"), null, progressHandler);

        verify(progressHandler, never()).onError(any());
        verify(progressHandler, never()).onComplete(any());
        verify(progressHandler, never()).onCanNotRenderer();
    }

    @Test
    public void providesPreview1() {
        setUpTestRenderers();
        PreviewProgressHandler progressHandler = mock(PreviewProgressHandler.class);
        previewProvider.getPreview(fsPathNode("file.txt"), null, progressHandler);

        verify(progressHandler, never()).onError(any());
        verify(progressHandler).onComplete(any());
        verify(progressHandler, never()).onCanNotRenderer();
    }

    @Test
    public void providesPreview2() {
        setUpTestRenderers();
        PreviewProgressHandler progressHandler = mock(PreviewProgressHandler.class);
        previewProvider.getPreview(fsPathNode("file.jpg"), null, progressHandler);

        verify(progressHandler, never()).onError(any());
        verify(progressHandler).onComplete(any());
        verify(progressHandler, never()).onCanNotRenderer();
    }

    @Test
    public void providesPreviewInArchive1() {
        setUpTestRenderers();
        PreviewProgressHandler progressHandler = mock(PreviewProgressHandler.class);
        previewProvider.getPreview(archiveEntryNode("file.txt"), null, progressHandler);

        verify(progressHandler, never()).onError(any());
        verify(progressHandler).onComplete(any());
        verify(progressHandler, never()).onCanNotRenderer();
    }

    @Test
    public void providesPreviewInArchive2() {
        setUpTestRenderers();
        PreviewProgressHandler progressHandler = mock(PreviewProgressHandler.class);
        previewProvider.getPreview(archiveEntryNode("file.jpg"), null, progressHandler);

        verify(progressHandler, never()).onError(any());
        verify(progressHandler).onComplete(any());
        verify(progressHandler, never()).onCanNotRenderer();
    }

    private void setUpNullRenderers() {
        previewProvider = new DefaultPreviewProvider(fsManager, archivesManager, null);
    }

    private void setUpFailingRenderers() {
        previewRenderers.clear();
        previewRenderers.addAll(getFailingRenderers());
    }

    private void setUpTestRenderers() {
        previewRenderers.clear();
        previewRenderers.addAll(getTestRenderers());
    }

    private static List<PreviewRenderer> getFailingRenderers() {
        PreviewRenderer txtPreviewRenderer = new PreviewRenderer() {
            @Override
            public boolean canRenderForExtension(String fileExtension) {
                return fileExtension.equals("txt");
            }

            @Override
            public JComponent render(PreviewRenderingData data) throws InterruptedException {
                return null;
            }
        };
        PreviewRenderer jpgPreviewRenderer = new PreviewRenderer() {
            @Override
            public boolean canRenderForExtension(String fileExtension) {
                return fileExtension.equals("jpg");
            }

            @Override
            public JComponent render(PreviewRenderingData data) throws InterruptedException {
                throw new InterruptedException();
            }
        };
        return Arrays.asList(txtPreviewRenderer, jpgPreviewRenderer);
    }

    private static List<PreviewRenderer> getTestRenderers() {
        PreviewRenderer txtPreviewRenderer = new PreviewRenderer() {
            @Override
            public boolean canRenderForExtension(String fileExtension) {
                return fileExtension.equals("txt");
            }

            @Override
            public JComponent render(PreviewRenderingData data) throws InterruptedException {
                return mock(JComponent.class);
            }
        };
        PreviewRenderer jpgPreviewRenderer = new PreviewRenderer() {
            @Override
            public boolean canRenderForExtension(String fileExtension) {
                return fileExtension.equals("jpg");
            }

            @Override
            public JComponent render(PreviewRenderingData data) throws InterruptedException {
                return mock(JComponent.class);
            }
        };
        return Arrays.asList(txtPreviewRenderer, jpgPreviewRenderer);
    }

    private static TreeNodeData fsPathNode(String lastComponent) {
        return new TreeNodeData("", new FsPath("/some/path", TargetType.FILE, lastComponent));
    }

    private static TreeNodeData archiveEntryNode(String lastComponent) {
        return new TreeNodeData(
                "", new ArchiveEntryPath(null, "some/entry", TargetType.FILE, lastComponent));
    }
}