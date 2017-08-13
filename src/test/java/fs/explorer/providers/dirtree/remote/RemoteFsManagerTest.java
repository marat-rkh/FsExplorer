package fs.explorer.providers.dirtree.remote;

import fs.explorer.TestEnvironment;
import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.path.TargetType;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static fs.explorer.providers.dirtree.remote.TestUtils.rebexTestServer;
import static fs.explorer.providers.dirtree.remote.TestUtils.tele2TestServer;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

public class RemoteFsManagerTest {
    @Before
    public void setUp() {
        Assume.assumeTrue(TestEnvironment.ftpTestsNeeded());
    }

    @Test
    public void checksConnection1() throws FTPException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(rebexTestServer());
        remoteFsManager.checkConnection();
    }

    @Test
    public void checksConnection2() throws FTPException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(tele2TestServer());
        remoteFsManager.checkConnection();
    }

    @Test
    public void readsTextFile() throws IOException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(rebexTestServer());
        byte[] bytes = remoteFsManager.readFile(testFsPath("/readme.txt", /*isDir*/false, ""));
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    @Test
    public void readsImageFile() throws IOException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(rebexTestServer());
        FsPath path = testFsPath("/pub/example/ConsoleClient.png", /*isDir*/false, "");
        byte[] bytes = remoteFsManager.readFile(path);
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    @Test
    public void readsZipFile1() throws IOException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(tele2TestServer());
        byte[] bytes = remoteFsManager.readFile(testFsPath("/1KB.zip", /*isDir*/false, ""));
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    @Test
    public void readsZipFile2() throws IOException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(tele2TestServer());
        byte[] bytes = remoteFsManager.readFile(testFsPath("/10MB.zip", /*isDir*/false, ""));
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    @Test(expected = IOException.class)
    public void failsToReadOnNullFsPath() throws IOException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(tele2TestServer());
        remoteFsManager.readFile(null);
    }

    @Test(expected = IOException.class)
    public void failsToReadOnNullPath() throws IOException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(tele2TestServer());
        remoteFsManager.readFile(testFsPath(null, /*isDir*/false, ""));
    }

    @Test(expected = IOException.class)
    public void failsToReadDirectory() throws IOException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(tele2TestServer());
        remoteFsManager.readFile(testFsPath("/", /*isDir*/false, ""));
    }

    @Test
    public void listsEntries() throws IOException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(rebexTestServer());
        List<FsPath> paths = remoteFsManager.list(testFsPath("/", /*isDir*/true, ""));
        assertEquals(2, paths.size());
        assertThat(paths, containsInAnyOrder(
                testFsPath("/pub", /*isDir*/true, "pub"),
                testFsPath("/readme.txt", /*isDir*/false, "readme.txt")
        ));
    }

    @Test
    public void listsEntriesWithCorrectPaths() throws IOException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(rebexTestServer());
        List<FsPath> paths = remoteFsManager.list(testFsPath("/pub", /*isDir*/true, ""));
        assertEquals(1, paths.size());
        assertThat(paths, containsInAnyOrder(
                testFsPath("/pub/example", /*isDir*/true, "example")
        ));
    }

    @Test(expected = IOException.class)
    public void failsToListEntriesOnFile() throws IOException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(rebexTestServer());
        remoteFsManager.list(testFsPath("/readme.txt", /*isDir*/false, ""));
    }

    @Test(expected = IOException.class)
    public void failsToListEntriesOnNullFsPath() throws IOException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(rebexTestServer());
        remoteFsManager.list(null);
    }

    @Test(expected = IOException.class)
    public void failsToListEntriesOnNullPath() throws IOException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(rebexTestServer());
        remoteFsManager.list(testFsPath(null, /*isDir*/true, ""));
    }

    // TODO consider changing this to failure as in LocalFsManager
    @Test
    public void listsNoEntriesOnInvalidPath() throws IOException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(rebexTestServer());
        FsPath path = testFsPath("/---===---", /*isDir*/true, "");
        List<FsPath> entries = remoteFsManager.list(path);
        assertNotNull(entries);
        assertEquals(0, entries.size());
    }

    // TODO consider changing this to failure as in LocalFsManager
    @Test
    public void listsNoEntriesOnNonExistingPath() throws IOException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(rebexTestServer());
        FsPath path = testFsPath("/my-dir", /*isDir*/true, "");
        List<FsPath> entries = remoteFsManager.list(path);
        assertNotNull(entries);
        assertEquals(0, entries.size());
    }

    @Test
    public void withFileStreamProvidesNotNullStream() throws IOException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(tele2TestServer());
        remoteFsManager.withFileStream(testFsPath("/1KB.zip", /*isDir*/false, ""), is -> {
            assertNotNull(is);
            return null;
        });
    }

    @Test
    public void withFileStreamCanBeCalledMultipleTimes() throws IOException {
        RemoteFsManager remoteFsManager = new RemoteFsManager(tele2TestServer());
        remoteFsManager.withFileStream(testFsPath("/1KB.zip", /*isDir*/false, ""), is -> {
            assertNotNull(is);
            return null;
        });
        remoteFsManager.withFileStream(testFsPath("/512KB.zip", /*isDir*/false, ""), is -> {
            assertNotNull(is);
            return null;
        });
    }

    private static FsPath testFsPath(String path, boolean isDir, String lastComponent) {
        TargetType targetType = isDir ? TargetType.DIRECTORY : TargetType.FILE;
        return new FsPath(path, targetType, lastComponent);
    }
}