package fs.explorer.providers.dirtree.remote;

import fs.explorer.TestEnvironment;
import fs.explorer.providers.dirtree.FsPath;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

public class RemoteFsManagerTest {
    private RemoteFsManager remoteFsManager;

    @Before
    public void setUp() {
        Assume.assumeTrue(TestEnvironment.ftpTestsNeeded());

        remoteFsManager = new RemoteFsManager();
    }

    @Test
    public void connectsToAndDisconnectsFromFTPServer() throws FTPException {
        try {
            remoteFsManager.connect(rebexTestServer());
            remoteFsManager.disconnect();
        } catch (FTPException e) {
            fail();
        }
    }

    @Test
    public void connectsToAndDisconnectsWithAnonymousUser() throws FTPException {
        try {
            remoteFsManager.connect(tele2TestServer());
            remoteFsManager.disconnect();
        } catch (FTPException e) {
            fail();
        }
    }

    @Test
    public void connectsToAndDisconnectsWithAnonymousUserImplicitly() throws FTPException {
        try {
            remoteFsManager.connect(tele2TestServerNoCredintails());
            remoteFsManager.disconnect();
        } catch (FTPException e) {
            fail();
        }
    }

    @Test(expected = FTPException.class)
    public void failsOnRepeatedConnect() throws FTPException {
        try {
            remoteFsManager.connect(tele2TestServerNoCredintails());
        } catch (FTPException e) {
            fail();
        }
        remoteFsManager.connect(tele2TestServer());
        try {
            remoteFsManager.disconnect();
        } catch (FTPException e) {
            fail();
        }
    }

    @Test
    public void readsTextFile() throws FTPException {
        try {
            remoteFsManager.connect(rebexTestServer());
            byte[] bytes = remoteFsManager.readFile(testFsPath("/readme.txt", /*isDir*/false, ""));
            assertNotNull(bytes);
            assertTrue(bytes.length > 0);
        } catch (FTPException | IOException e) {
            fail();
        } finally {
            remoteFsManager.disconnect();
        }
    }

    @Test
    public void readsImageFile() throws FTPException {
        try {
            remoteFsManager.connect(rebexTestServer());
            FsPath path = testFsPath("/pub/example/ConsoleClient.png", /*isDir*/false, "");
            byte[] bytes = remoteFsManager.readFile(path);
            assertNotNull(bytes);
            assertTrue(bytes.length > 0);
        } catch (FTPException | IOException e) {
            fail();
        } finally {
            remoteFsManager.disconnect();
        }
    }

    @Test
    public void readsZipFile() throws FTPException {
        try {
            remoteFsManager.connect(tele2TestServer());
            byte[] bytes = remoteFsManager.readFile(testFsPath("/1KB.zip", /*isDir*/false, ""));
            assertNotNull(bytes);
            assertTrue(bytes.length > 0);
        } catch (FTPException | IOException e) {
            fail();
        } finally {
            remoteFsManager.disconnect();
        }
    }

    @Test(expected = IOException.class)
    public void failsToReadOnNullFsPath() throws FTPException, IOException {
        try {
            remoteFsManager.connect(tele2TestServer());
            remoteFsManager.readFile(null);
        } catch (FTPException e) {
            fail();
        } finally {
            remoteFsManager.disconnect();
        }
    }

    @Test(expected = IOException.class)
    public void failsToReadOnNullPath() throws FTPException, IOException {
        try {
            remoteFsManager.connect(tele2TestServer());
            remoteFsManager.readFile(testFsPath(null, /*isDir*/false, ""));
        } catch (FTPException e) {
            fail();
        } finally {
            remoteFsManager.disconnect();
        }
    }

    @Test(expected = IOException.class)
    public void failsToReadDirectory() throws FTPException, IOException {
        try {
            remoteFsManager.connect(tele2TestServer());
            remoteFsManager.readFile(testFsPath("/", /*isDir*/false, ""));
        } catch (FTPException e) {
            fail();
        } finally {
            remoteFsManager.disconnect();
        }
    }

    @Test
    public void listsEntries() throws FTPException {
        try {
            remoteFsManager.connect(rebexTestServer());
            List<FsPath> paths = remoteFsManager.list(testFsPath("/", /*isDir*/true, ""));
            assertEquals(2, paths.size());
            assertThat(paths, containsInAnyOrder(
                    testFsPath("/pub", /*isDir*/true, "pub"),
                    testFsPath("/readme.txt", /*isDir*/false, "readme.txt")
            ));
        } catch (FTPException | IOException e) {
            fail();
        } finally {
            remoteFsManager.disconnect();
        }
    }

    @Test
    public void listsEntriesWithCorrectPaths() throws FTPException {
        try {
            remoteFsManager.connect(rebexTestServer());
            List<FsPath> paths = remoteFsManager.list(testFsPath("/pub", /*isDir*/true, ""));
            assertEquals(1, paths.size());
            assertThat(paths, containsInAnyOrder(
                    testFsPath("/pub/example", /*isDir*/true, "example")
            ));
        } catch (FTPException | IOException e) {
            fail();
        } finally {
            remoteFsManager.disconnect();
        }
    }

    @Test(expected = IOException.class)
    public void failsToListEntriesOnFile() throws FTPException, IOException {
        try {
            remoteFsManager.connect(rebexTestServer());
            remoteFsManager.list(testFsPath("/readme.txt", /*isDir*/false, ""));
        } catch (FTPException e) {
            fail();
        } finally {
            remoteFsManager.disconnect();
        }
    }

    @Test(expected = IOException.class)
    public void failsToListEntriesOnNullFsPath() throws FTPException, IOException {
        try {
            remoteFsManager.connect(rebexTestServer());
            remoteFsManager.list(null);
        } catch (FTPException e) {
            fail();
        } finally {
            remoteFsManager.disconnect();
        }
    }

    @Test(expected = IOException.class)
    public void failsToListEntriesOnNullPath() throws FTPException, IOException {
        try {
            remoteFsManager.connect(rebexTestServer());
            remoteFsManager.list(testFsPath(null, /*isDir*/true, ""));
        } catch (FTPException e) {
            fail();
        } finally {
            remoteFsManager.disconnect();
        }
    }

    // TODO consider changing this to failure as in LocalFsManager
    @Test
    public void listsNoEntriesOnInvalidPath() throws FTPException {
        try {
            remoteFsManager.connect(rebexTestServer());
            FsPath path = testFsPath("/---===---", /*isDir*/true, "");
            List<FsPath> entries = remoteFsManager.list(path);
            assertNotNull(entries);
            assertEquals(0, entries.size());
        } catch (FTPException | IOException e) {
            fail();
        } finally {
            remoteFsManager.disconnect();
        }
    }

    // TODO consider changing this to failure as in LocalFsManager
    @Test
    public void listsNoEntriesOnNonExistingPath() throws FTPException {
        try {
            remoteFsManager.connect(rebexTestServer());
            FsPath path = testFsPath("/my-dir", /*isDir*/true, "");
            List<FsPath> entries = remoteFsManager.list(path);
            assertNotNull(entries);
            assertEquals(0, entries.size());
        } catch (FTPException | IOException e) {
            fail();
        } finally {
            remoteFsManager.disconnect();
        }
    }

    private static FTPConnectionInfo rebexTestServer() {
        return new FTPConnectionInfo("test.rebex.net", "demo", "password".toCharArray());
    }

    private static FTPConnectionInfo tele2TestServer() {
        return new FTPConnectionInfo("speedtest.tele2.net", "anonymous", "".toCharArray());
    }

    private static FTPConnectionInfo tele2TestServerNoCredintails() {
        return new FTPConnectionInfo("speedtest.tele2.net", "", "".toCharArray());
    }

    private static FsPath testFsPath(String path, boolean isDir, String lastComponent) {
        return new FsPath(path, isDir, lastComponent);
    }
}