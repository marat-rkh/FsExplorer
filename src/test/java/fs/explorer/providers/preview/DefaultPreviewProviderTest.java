package fs.explorer.providers.preview;

import fs.explorer.providers.dirtree.FsManager;
import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.TestUtils;
import fs.explorer.providers.dirtree.path.FsPath.TargetType;
import fs.explorer.providers.dirtree.TreeNodeData;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.io.IOException;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

public class DefaultPreviewProviderTest {
    private FsManager fsManager;
    private PreviewRenderer previewRenderer;
    private DefaultPreviewProvider previewProvider;

    @Before
    public void setUp() throws IOException {
        fsManager = mock(FsManager.class);
        previewRenderer = mock(PreviewRenderer.class);
        when(fsManager.readFile(any())).thenReturn(new byte[1]);
        when(previewRenderer.renderText(any())).thenReturn(new JTextArea());
        when(previewRenderer.renderImage(any())).thenReturn(new JLabel());
        previewProvider = new DefaultPreviewProvider(fsManager, previewRenderer);
    }

    @Test
    public void providesTextPreview() {
        Consumer<JComponent> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        previewProvider.getTextPreview(nodeData, onComplete, onFail);

        verify(onComplete).accept(any());
        verify(onFail, never()).accept(any());
    }

    @Test
    public void doesNotProvideTextPreviewOnNullData() {
        Consumer<JComponent> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        previewProvider.getTextPreview(null, onComplete, onFail);

        verify(onComplete, never()).accept(any());
        verify(onFail).accept(any());
    }

    @Test
    public void doesNotProvideTextPreviewOnNullFileContents() throws IOException {
        when(fsManager.readFile(any())).thenReturn(null);
        Consumer<JComponent> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        previewProvider.getTextPreview(nodeData, onComplete, onFail);

        verify(onComplete, never()).accept(any());
        verify(onFail).accept(any());
    }

    @Test
    public void doesNotProvideTextPreviewOnFsManagerFail() throws IOException {
        when(fsManager.readFile(any())).thenThrow(new IOException());
        Consumer<JComponent> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        previewProvider.getTextPreview(nodeData, onComplete, onFail);

        verify(onComplete, never()).accept(any());
        verify(onFail).accept(any());
    }

    @Test
    public void doesNotProvideTextPreviewOnRenderingFail() throws IOException {
        when(previewRenderer.renderText(any())).thenReturn(null);
        Consumer<JComponent> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        previewProvider.getTextPreview(nodeData, onComplete, onFail);

        verify(onComplete, never()).accept(any());
        verify(onFail).accept(any());
    }

    @Test
    public void providesImagePreview() {
        Consumer<JComponent> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        previewProvider.getImagePreview(nodeData, onComplete, onFail);

        verify(onComplete).accept(any());
        verify(onFail, never()).accept(any());
    }

    @Test
    public void doesNotProvideImagePreviewOnNullData() {
        Consumer<JComponent> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        previewProvider.getImagePreview(null, onComplete, onFail);

        verify(onComplete, never()).accept(any());
        verify(onFail).accept(any());
    }

    @Test
    public void doesNotProvideImagePreviewOnNullFileContents() throws IOException {
        when(fsManager.readFile(any())).thenReturn(null);
        Consumer<JComponent> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        previewProvider.getImagePreview(nodeData, onComplete, onFail);

        verify(onComplete, never()).accept(any());
        verify(onFail).accept(any());
    }

    @Test
    public void doesNotProvideImagePreviewOnFsManagerFail() throws IOException {
        when(fsManager.readFile(any())).thenThrow(new IOException());
        Consumer<JComponent> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        previewProvider.getImagePreview(nodeData, onComplete, onFail);

        verify(onComplete, never()).accept(any());
        verify(onFail).accept(any());
    }

    @Test
    public void doesNotProvideImagePreviewOnRenderingFail() throws IOException {
        when(previewRenderer.renderImage(any())).thenReturn(null);
        Consumer<JComponent> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        previewProvider.getImagePreview(nodeData, onComplete, onFail);

        verify(onComplete, never()).accept(any());
        verify(onFail).accept(any());
    }

    private static TreeNodeData nodeData =
            new TreeNodeData("", new FsPath("/some/path", TargetType.FILE, ""));
}