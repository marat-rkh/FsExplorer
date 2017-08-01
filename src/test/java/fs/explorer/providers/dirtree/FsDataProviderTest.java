package fs.explorer.providers.dirtree;

import fs.explorer.providers.TestUtils;
import fs.explorer.providers.dirtree.FsDataProvider;
import fs.explorer.providers.dirtree.FsManager;
import fs.explorer.providers.dirtree.FsPath;
import fs.explorer.providers.dirtree.TreeNodeData;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FsDataProviderTest {
    private FsPath topDir;
    private FsManager fsManager;
    private FsDataProvider fsDataProvider;

    @Before
    public void setUp() throws URISyntaxException, IOException {
        fsManager = mock(FsManager.class);
        topDir = new FsPath("/some/dir", /*isDirectory*/true, "dir");
        fsDataProvider = new FsDataProvider(topDir, fsManager);
    }

    @Test
    public void providesValidTopNode() {
        CapturingConsumer<TreeNodeData> onCompete = spy(new CapturingConsumer<>());
        fsDataProvider.getTopNode(onCompete);

        verify(onCompete).accept(any());
        TreeNodeData topNodeData = onCompete.getValue();
        assertEquals("dir", topNodeData.getLabel());
        assertEquals(topDir, topNodeData.getFsPath());
    }

    @Test
    public void providesNodesForDirInSortedOrder() throws IOException, URISyntaxException {
        setUpTestDirs();
        CapturingConsumer<List<TreeNodeData>> onComplete = new CapturingConsumer<>();
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        fsDataProvider.getNodesFor(nodeData(/*isDir*/true), onComplete, onFail);

        List<TreeNodeData> nodes = onComplete.getValue();
        assertNotNull(nodes);
        assertEquals(5, nodes.size());
        // sorted directories come first
        checkNodeData("documents", "/home/documents", /*isDir*/true, "documents", nodes.get(0));
        checkNodeData("music", "/home/music", /*isDir*/true, "music", nodes.get(1));
        checkNodeData("pics", "/home/pics", /*isDir*/true, "pics", nodes.get(2));
        // then go sorted files
        checkNodeData("draft.txt", "/home/draft.txt", /*isDir*/false, "draft.txt", nodes.get(3));
        checkNodeData("my-text.txt", "/home/my-text.txt", /*isDir*/false, "my-text.txt",
                nodes.get(4));
        verify(onFail, never()).accept(any());
    }

    @Test
    public void doesNotProvideNodesForFile() {
        Consumer<List<TreeNodeData>> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        fsDataProvider.getNodesFor(nodeData(/*isDir*/false), onComplete, onFail);

        verify(onComplete, never()).accept(any());
        verify(onFail).accept(any());
    }

    @Test
    public void doesNotProvideNodesOnFsManagerFail() throws IOException {
        when(fsManager.list(any())).thenThrow(new IOException());
        Consumer<List<TreeNodeData>> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        fsDataProvider.getNodesFor(nodeData(/*isDir*/true), onComplete, onFail);

        verify(onComplete, never()).accept(any());
        verify(onFail).accept(any());
    }

    @Test
    public void correctsNodeLabelOnFsPathWithEmptyLastComponent() throws IOException {
        when(fsManager.list(any())).thenReturn(Collections.singletonList(
                new FsPath("", /*isDirectory*/true, "")
        ));
        CapturingConsumer<List<TreeNodeData>> onComplete = spy(new CapturingConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        fsDataProvider.getNodesFor(nodeData(/*isDir*/true), onComplete, onFail);

        List<TreeNodeData> nodes = onComplete.getValue();
        assertNotNull(nodes);
        assertEquals(1, nodes.size());
        checkNodeData("?", "", /*isDir*/true, "", nodes.get(0));
        verify(onFail, never()).accept(any());
    }

    // TODO test null TreeNodeData passed to getNodesFor
    // TODO test null entries list returned from fsManager in getNodesFor

    private void setUpTestDirs() throws IOException {
        List<FsPath> paths = Arrays.asList(
                new FsPath("/home/documents", /*isDirectory*/true, "documents"),
                new FsPath("/home/music", /*isDirectory*/true, "music"),
                new FsPath("/home/pics", /*isDirectory*/true, "pics"),
                new FsPath("/home/my-text.txt", /*isDirectory*/false, "my-text.txt"),
                new FsPath("/home/draft.txt", /*isDirectory*/false, "draft.txt")
        );
        Collections.shuffle(paths);
        when(fsManager.list(any())).thenReturn(paths);
    }

    private static void checkNodeData(
            String label,
            String path,
            boolean isDir,
            String lastComponent,
            TreeNodeData actual
    ) {
        assertEquals(label, actual.getLabel());
        assertEquals(path, actual.getFsPath().getPath());
        assertEquals(isDir, actual.getFsPath().isDirectory());
        assertEquals(lastComponent, actual.getFsPath().getLastComponent());
    }

    private static TreeNodeData nodeData(boolean isDir) {
        return new TreeNodeData("", new FsPath("", isDir, ""));
    }

    private static class CapturingConsumer<T> implements Consumer<T> {
        private T value;

        @Override
        public void accept(T t) {
            value = t;
        }

        public T getValue() { return value; }
    }
}