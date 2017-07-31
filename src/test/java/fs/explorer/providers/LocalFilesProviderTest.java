package fs.explorer.providers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LocalFilesProviderTest {
    private LocalFilesProvider localFilesProvider;
    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    @Before
    public void setUp() throws URISyntaxException, IOException {
        localFilesProvider = new LocalFilesProvider();
    }

    @Test
    public void providesValidTopNode() {
        localFilesProvider.getTopNode(topNode -> {
            assertTrue(topNode.getLabel().equals("/") ||
                    topNode.getLabel().equalsIgnoreCase("C:\\"));
            assertTrue(topNode.getFsPath().getPath().equals("/") ||
                    topNode.getFsPath().getPath().equalsIgnoreCase("C:\\"));
            assertTrue(topNode.getFsPath().isDirectory());
        });
    }

    @Test
    public void providesNodesForDir() throws IOException, URISyntaxException {
        setUpTestDirs();
        TreeNodeData data = nodeData(testPath("/home"), /*isDir*/true);
        CapturingConsumer<List<TreeNodeData>> onComplete = new CapturingConsumer<>();
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        localFilesProvider.getNodesFor(data, onComplete, onFail);

        List<TreeNodeData> nodes = onComplete.getValue();
        assertNotNull(nodes);
        assertEquals(3, nodes.size());
        checkNodeData("documents", testPath("/home/documents"), /*isDir*/true, nodes.get(0));
        checkNodeData("music", testPath("/home/music"), /*isDir*/true, nodes.get(1));
        checkNodeData("my-text.txt", testPath("/home/my-text.txt"), /*isDir*/false, nodes.get(2));
        verify(onFail, never()).accept(any());
    }

    @Test
    public void doesNotProvideNodesForFile() {
        TreeNodeData data = nodeData(testPath("/home"), /*isDir*/false);
        Consumer<List<TreeNodeData>> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        localFilesProvider.getNodesFor(data, onComplete, onFail);

        verify(onComplete, never()).accept(any());
        verify(onFail).accept(any());
    }

    @Test
    public void doesNotProvideNodesOnInvalidPath() {
        TreeNodeData data = nodeData(testPath("-------"), /*isDir*/false);
        Consumer<List<TreeNodeData>> onComplete = spy(new TestUtils.DummyConsumer<>());
        Consumer<String> onFail = spy(new TestUtils.DummyConsumer<>());
        localFilesProvider.getNodesFor(data, onComplete, onFail);

        verify(onComplete, never()).accept(any());
        verify(onFail).accept(any());
    }

    private void setUpTestDirs() throws URISyntaxException, IOException {
        Path testDir = Paths.get(getClass().getResource("/testdirs/home").toURI());
        TestUtils.copyDirectory(testDir, tmpDir.getRoot().toPath());
    }

    private static void checkNodeData(
            String label, String path, boolean isDir, TreeNodeData actual) {
        assertEquals(actual.getLabel(), label);
        assertEquals(actual.getFsPath().getPath(), path);
        assertEquals(actual.getFsPath().isDirectory(), isDir);
    }

    private static TreeNodeData nodeData(String path, boolean isDir) {
        return new TreeNodeData("", new FsPath(path, isDir));
    }

    private String testPath(String relativePath) {
        return Paths.get(tmpDir.getRoot().toPath().toString(), relativePath).toString();
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