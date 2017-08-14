package fs.explorer.providers.dirtree;

import fs.explorer.providers.TestUtils;
import fs.explorer.providers.dirtree.archives.ArchivesManager;
import fs.explorer.providers.dirtree.path.ArchiveEntryPath;
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultFsDataProviderTest {
    private FsPath topDir;
    private FsManager fsManager;
    private ArchivesManager archivesManager;
    private DefaultFsDataProvider fsDataProvider;

    @Before
    public void setUp() throws URISyntaxException, IOException {
        fsManager = mock(FsManager.class);
        archivesManager = mock(ArchivesManager.class);
        topDir = new FsPath("/some/dir", TargetType.DIRECTORY, "dir");
        fsDataProvider = new DefaultFsDataProvider(topDir, fsManager, archivesManager);
    }

    @Test
    public void providesValidTopNode() {
        CapturingConsumer<TreeNodeData> onCompete = spy(new CapturingConsumer<>());
        fsDataProvider.getTopNode(onCompete);

        verify(onCompete).accept(any());
        TreeNodeData topNodeData = onCompete.getValue();
        checkFsPathNode("dir", "/some/dir", /*isDir*/true, "dir", topNodeData);
    }

    @Test
    public void providesNodesForDirInSortedOrder() throws IOException, URISyntaxException {
        setUpFsManagerTestDirs();
        CapturingConsumer<List<TreeNodeData>> onComplete = new CapturingConsumer<>();
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        fsDataProvider.getNodesFor(fsPathNode(/*isDir*/true), onComplete, onFail);

        List<TreeNodeData> nodes = onComplete.getValue();
        assertNotNull(nodes);
        assertEquals(5, nodes.size());
        // sorted directories come first
        checkFsPathNode(
                "documents", "/home/documents", /*isDir*/true, "documents",
                nodes.get(0)
        );
        checkFsPathNode("music", "/home/music", /*isDir*/true, "music", nodes.get(1));
        checkFsPathNode("pics", "/home/pics", /*isDir*/true, "pics", nodes.get(2));
        // then go sorted files
        checkFsPathNode(
                "draft.txt", "/home/draft.txt", /*isDir*/false, "draft.txt",
                nodes.get(3)
        );
        checkFsPathNode(
                "my-text.txt", "/home/my-text.txt", /*isDir*/false, "my-text.txt",
                nodes.get(4)
        );
        verify(onFail, never()).accept(any());
    }

    @Test
    public void doesNotProvideNodesForFile() {
        Consumer<List<TreeNodeData>> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        fsDataProvider.getNodesFor(fsPathNode(/*isDir*/false), onComplete, onFail);

        verify(onComplete, never()).accept(any());
        verify(onFail).accept(any());
    }

    @Test
    public void doesNotProvideNodesOnFsManagerFail() throws IOException {
        when(fsManager.list(any())).thenThrow(new IOException());
        Consumer<List<TreeNodeData>> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        fsDataProvider.getNodesFor(fsPathNode(/*isDir*/true), onComplete, onFail);

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
        fsDataProvider.getNodesFor(fsPathNode(/*isDir*/true), onComplete, onFail);

        List<TreeNodeData> nodes = onComplete.getValue();
        assertNotNull(nodes);
        assertEquals(1, nodes.size());
        checkFsPathNode("?", "", /*isDir*/true, "", nodes.get(0));
        verify(onFail, never()).accept(any());
    }

    @Test
    public void doesNotProvideNodesForNullNode() throws IOException {
        Consumer<List<TreeNodeData>> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        fsDataProvider.getNodesFor(null, onComplete, onFail);

        verify(onComplete, never()).accept(any());
        verify(onFail).accept(any());
    }

    @Test
    public void doesNotProvideNodesOnNullFromFsManager() throws IOException {
        when(fsManager.list(any())).thenReturn(null);
        Consumer<List<TreeNodeData>> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        fsDataProvider.getNodesFor(fsPathNode(/*isDir*/true), onComplete, onFail);

        verify(onComplete, never()).accept(any());
        verify(onFail).accept(any());
    }

    @Test
    public void providesNodesForZipArchive() throws IOException {
        FsPath archivePath = new FsPath("", TargetType.ZIP_ARCHIVE, "");
        setUpArchiveManagerTestDirs(archivePath);
        CapturingConsumer<List<TreeNodeData>> onComplete = new CapturingConsumer<>();
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        fsDataProvider.getNodesFor(fsPathNode(TargetType.ZIP_ARCHIVE), onComplete, onFail);

        verify(onFail, never()).accept(any());
        List<TreeNodeData> nodes = onComplete.getValue();
        assertNotNull(nodes);
        assertEquals(2, nodes.size());
        checkArchiveEntryNode(
                "docs", archivePath, "docs/", TargetType.DIRECTORY, "docs",
                nodes.get(0)
        );
        checkArchiveEntryNode(
                "scans", archivePath, "scans/", TargetType.DIRECTORY, "scans",
                nodes.get(1)
        );
    }

    @Test
    public void doesNotProvideNodesForZipArchiveOnNullFromArchiveManager() throws IOException {
        when(archivesManager.listArchive(any(), any())).thenReturn(null);
        Consumer<List<TreeNodeData>> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        fsDataProvider.getNodesFor(fsPathNode(TargetType.ZIP_ARCHIVE), onComplete, onFail);

        verify(onComplete, never()).accept(any());
        verify(onFail).accept(any());
    }

    @Test
    public void providesNodesForDirectoryInsideZipArchive() throws IOException {
        FsPath archivePath = new FsPath("", TargetType.ZIP_ARCHIVE, "");
        setUpArchiveManagerTestDirs(archivePath);
        CapturingConsumer<List<TreeNodeData>> onComplete = new CapturingConsumer<>();
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        fsDataProvider.getNodesFor(
                archiveEntryNode(TargetType.DIRECTORY), onComplete, onFail);

        verify(onFail, never()).accept(any());
        List<TreeNodeData> nodes = onComplete.getValue();
        assertNotNull(nodes);
        assertEquals(1, nodes.size());
        checkArchiveEntryNode(
                "all.zip", archivePath, "scans/all.zip", TargetType.ZIP_ARCHIVE, "all.zip",
                nodes.get(0)
        );
    }

    @Test
    public void providesNodesForZipFileInsideZipArchive() throws IOException {
        FsPath archivePath = new FsPath("", TargetType.ZIP_ARCHIVE, "");
        setUpArchiveManagerTestDirs(archivePath);
        CapturingConsumer<List<TreeNodeData>> onComplete = new CapturingConsumer<>();
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        fsDataProvider.getNodesFor(
                archiveEntryNode(TargetType.ZIP_ARCHIVE), onComplete, onFail);

        verify(onFail, never()).accept(any());
        List<TreeNodeData> nodes = onComplete.getValue();
        assertNotNull(nodes);
        assertEquals(1, nodes.size());
        checkArchiveEntryNode(
                "all.zip", archivePath, "scans/all.zip", TargetType.ZIP_ARCHIVE, "all.zip",
                nodes.get(0)
        );
    }

    @Test
    public void doesNotProvidesNodesForFileInsideZipArchive() throws IOException {
        FsPath archivePath = new FsPath("", TargetType.ZIP_ARCHIVE, "");
        setUpArchiveManagerTestDirs(archivePath);
        Consumer<List<TreeNodeData>> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        fsDataProvider.getNodesFor(archiveEntryNode(TargetType.FILE), onComplete, onFail);

        verify(onFail).accept(any());
        verify(onComplete, never()).accept(any());
    }

    @Test
    public void doesNotProvideNodesForZipArchiveEntryOnNullFromArchiveManager() throws IOException {
        when(archivesManager.listSubEntry(any(), any())).thenReturn(null);
        Consumer<List<TreeNodeData>> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        fsDataProvider.getNodesFor(
                archiveEntryNode(TargetType.DIRECTORY), onComplete, onFail);

        verify(onComplete, never()).accept(any());
        verify(onFail).accept(any());
    }

    private void setUpFsManagerTestDirs() throws IOException {
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

    private void setUpArchiveManagerTestDirs(FsPath archivePath) throws IOException {
        List<ArchiveEntryPath> paths = Arrays.asList(
                new ArchiveEntryPath(archivePath, "docs/", TargetType.DIRECTORY, "docs"),
                new ArchiveEntryPath(archivePath, "scans/", TargetType.DIRECTORY, "scans")
        );
        List<ArchiveEntryPath> subPaths = Collections.singletonList(
                new ArchiveEntryPath(
                        archivePath, "scans/all.zip", TargetType.ZIP_ARCHIVE, "all.zip")
        );
        Collections.shuffle(paths);
        when(archivesManager.listArchive(any(), any())).thenReturn(paths);
        when(archivesManager.listSubEntry(any(), any())).thenReturn(subPaths);
    }

    private static void checkFsPathNode(
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

    private static void checkArchiveEntryNode(
            String label,
            FsPath archivePath,
            String entryPath,
            TargetType targetType,
            String lastComponent,
            TreeNodeData actual
    ) {
        assertEquals(label, actual.getLabel());
        assertTrue(actual.getPath().isArchiveEntryPath());
        ArchiveEntryPath actualPath = actual.getPath().asArchiveEntryPath();
        assertEquals(archivePath, actualPath.getArchivePath());
        assertEquals(entryPath, actualPath.getEntryPath());
        assertEquals(targetType, actualPath.getTargetType());
        assertEquals(lastComponent, actualPath.getLastComponent());
    }

    private static TreeNodeData fsPathNode(boolean isDir) {
        TargetType targetType = isDir ? TargetType.DIRECTORY : TargetType.FILE;
        return new TreeNodeData("", new FsPath("", targetType, ""));
    }

    private static TreeNodeData fsPathNode(TargetType targetType) {
        return new TreeNodeData("", new FsPath("", targetType, ""));
    }

    private static TreeNodeData archiveEntryNode(TargetType targetType) {
        return new TreeNodeData("", new ArchiveEntryPath(null, "", targetType, ""));
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