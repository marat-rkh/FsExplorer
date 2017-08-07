package fs.explorer.providers.dirtree.local;

import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.path.TargetType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

public class LocalFsManagerTest {
    private LocalFsManager localFsManager = new LocalFsManager();
    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    @Test
    public void readsFile() throws URISyntaxException, IOException {
        FsPath fsPath = testFsPath("/testdirs/home/my-text.txt", /*isDir*/false, "");
        byte[] data = localFsManager.readFile(fsPath);
        assertEquals("some text", new String(data));
    }

    @Test
    public void readsZipFile() throws URISyntaxException, IOException {
        FsPath fsPath = testFsPath("/zips/home.zip", /*isDir*/false, "");
        byte[] data = localFsManager.readFile(fsPath);
        assertNotNull(data);
        assertTrue(data.length > 0);
    }

    @Test(expected = IOException.class)
    public void failsToReadOnNullFsPath() throws URISyntaxException, IOException {
        localFsManager.readFile(null);
    }

    @Test(expected = IOException.class)
    public void failsToReadOnNullPath() throws URISyntaxException, IOException {
        localFsManager.readFile(new FsPath(null, TargetType.DIRECTORY, ""));
    }

    @Test(expected = IOException.class)
    public void failsToReadDirectory() throws URISyntaxException, IOException {
        FsPath fsPath = testFsPath("/testdirs/home", /*isDir*/true, "");
        localFsManager.readFile(fsPath);
    }

    @Test(expected = IOException.class)
    public void failsToReadFileThatIsActuallyADirectory() throws URISyntaxException, IOException {
        FsPath fsPath = testFsPath("/testdirs/home", /*isDir*/false, "");
        localFsManager.readFile(fsPath);
    }

    @Test
    public void listsEntries() throws URISyntaxException, IOException {
        FsPath fsPath = testFsPath("/testdirs/home", /*isDir*/true, "");
        List<FsPath> paths = localFsManager.list(fsPath);
        assertNotNull(paths);
        assertEquals(5, paths.size());
        assertThat(paths, containsInAnyOrder(
                testFsPath("/testdirs/home/documents", /*isDir*/true, "documents"),
                testFsPath("/testdirs/home/music", /*isDir*/true, "music"),
                testFsPath("/testdirs/home/pics", /*isDir*/true, "pics"),
                testFsPath("/testdirs/home/draft.txt", /*isDir*/false, "draft.txt"),
                testFsPath("/testdirs/home/my-text.txt", /*isDir*/false, "my-text.txt")
        ));
    }


    @Test(expected = IOException.class)
    public void failsToListEntriesOnFile() throws URISyntaxException, IOException {
        FsPath fsPath = testFsPath("/testdirs/home/draft.txt", /*isDir*/false, "");
        localFsManager.list(fsPath);
    }

    @Test(expected = IOException.class)
    public void failsToListEntriesOnDirectoryThatIsActuallyAFile() throws URISyntaxException, IOException {
        FsPath fsPath = testFsPath("/testdirs/home/draft.txt", /*isDir*/true, "");
        localFsManager.list(fsPath);
    }

    @Test(expected = IOException.class)
    public void failsToListEntriesOnNullFsPath() throws IOException {
        localFsManager.list(null);
    }

    @Test(expected = IOException.class)
    public void failsToListEntriesOnNullPath() throws IOException, URISyntaxException {
        localFsManager.list(new FsPath(null, TargetType.DIRECTORY, ""));
    }

    @Test(expected = IOException.class)
    public void failsToListEntriesOnInvalidPath() throws URISyntaxException, IOException {
        FsPath fsPath = new FsPath("---===---", TargetType.DIRECTORY, "");
        localFsManager.readFile(fsPath);
    }

    @Test(expected = IOException.class)
    public void failsToListEntriesOnNonExistingPath() throws URISyntaxException, IOException {
        FsPath testDirPath = testFsPath("/testdirs", /*isDir*/true, "");
        String nonExistingPath = Paths.get(testDirPath.getPath(), "/home123").toString();
        FsPath fsPath = new FsPath(nonExistingPath, TargetType.DIRECTORY, "");
        localFsManager.readFile(fsPath);
    }

    @Test
    public void withFileStreamProvidesNonNullStream() throws URISyntaxException, IOException {
        FsPath fsPath = testFsPath("/testdirs/home/my-text.txt", /*isDir*/false, "");
        localFsManager.withFileStream(fsPath, is -> {
            assertNotNull(is);
            return null;
        });
    }

    private FsPath testFsPath(
            String relativePath,
            boolean isDir,
            String lastComponent
    ) throws URISyntaxException {
        Path dirPath = Paths.get(getClass().getResource(relativePath).toURI());
        TargetType targetType = isDir ? TargetType.DIRECTORY : TargetType.FILE;
        return new FsPath(dirPath.toString(), targetType, lastComponent);
    }

}