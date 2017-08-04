package fs.explorer.providers.dirtree;

import fs.explorer.providers.TestUtils;
import fs.explorer.providers.dirtree.archives.ArchivesManager;
import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.path.TargetType;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class FsDataProviderTest {
    private FsPath topDir;
    private FsManager fsManager;
    private ArchivesManager archivesManager;
    private FsDataProvider fsDataProvider;

    @Before
    public void setUp() throws URISyntaxException, IOException {
        fsManager = mock(FsManager.class);
        archivesManager = mock(ArchivesManager.class);
        topDir = new FsPath("/some/dir", TargetType.DIRECTORY, "dir");
        fsDataProvider = new FsDataProvider(topDir, fsManager, archivesManager);
    }

    @Test
    public void providesValidTopNode() {
        CapturingConsumer<TreeNodeData> onCompete = spy(new CapturingConsumer<>());
        fsDataProvider.getTopNode(onCompete);

        verify(onCompete).accept(any());
        TreeNodeData topNodeData = onCompete.getValue();
        checkFsPathNodeData("dir", "/some/dir", /*isDir*/true, "dir", topNodeData);
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
        checkFsPathNodeData("documents", "/home/documents", /*isDir*/true, "documents",
                nodes.get(0));
        checkFsPathNodeData("music", "/home/music", /*isDir*/true, "music", nodes.get(1));
        checkFsPathNodeData("pics", "/home/pics", /*isDir*/true, "pics", nodes.get(2));
        // then go sorted files
        checkFsPathNodeData("draft.txt", "/home/draft.txt", /*isDir*/false, "draft.txt",
                nodes.get(3));
        checkFsPathNodeData("my-text.txt", "/home/my-text.txt", /*isDir*/false, "my-text.txt",
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
                new FsPath("", TargetType.DIRECTORY, "")
        ));
        CapturingConsumer<List<TreeNodeData>> onComplete = spy(new CapturingConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        fsDataProvider.getNodesFor(nodeData(/*isDir*/true), onComplete, onFail);

        List<TreeNodeData> nodes = onComplete.getValue();
        assertNotNull(nodes);
        assertEquals(1, nodes.size());
        checkFsPathNodeData("?", "", /*isDir*/true, "", nodes.get(0));
        verify(onFail, never()).accept(any());
    }

    // TODO test null TreeNodeData passed to getNodesFor
    // TODO test null entries list returned from fsManager in getNodesFor

    private void setUpTestDirs() throws IOException {
        List<FsPath> paths = Arrays.asList(
                new FsPath("/home/documents", TargetType.DIRECTORY, "documents"),
                new FsPath("/home/music", TargetType.DIRECTORY, "music"),
                new FsPath("/home/pics", TargetType.DIRECTORY, "pics"),
                new FsPath("/home/my-text.txt", TargetType.FILE, "my-text.txt"),
                new FsPath("/home/draft.txt", TargetType.FILE, "draft.txt")
        );
        Collections.shuffle(paths);
        when(fsManager.list(any())).thenReturn(paths);
    }

    private static void checkFsPathNodeData(
            String label,
            String path,
            boolean isDir,
            String lastComponent,
            TreeNodeData actual
    ) {
        assertEquals(label, actual.getLabel());
        assertTrue(actual.getPath().isFsPath());
        FsPath actualPath = actual.getPath().asFsPath();
        assertEquals(path, actualPath.getPath());
        assertEquals(isDir, actualPath.isDirectory());
        assertEquals(lastComponent, actualPath.getLastComponent());
    }

    private static TreeNodeData nodeData(boolean isDir) {
        TargetType targetType = isDir ? TargetType.DIRECTORY : TargetType.FILE;
        return new TreeNodeData("", new FsPath("", targetType, ""));
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