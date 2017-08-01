package fs.explorer.providers.dirtree;

import fs.explorer.providers.dirtree.FsPath;
import fs.explorer.providers.dirtree.LocalFsManager;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

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
    public void failsToListEntriesOnBadPath() throws URISyntaxException, IOException {
        FsPath fsPath = new FsPath("---===---", /*isDirectory*/true, "");
        localFsManager.readFile(fsPath);
    }

    private FsPath testFsPath(
            String relativePath,
            boolean isDir,
            String lastComponent
    ) throws URISyntaxException {
        Path dirPath = Paths.get(getClass().getResource(relativePath).toURI());
        return new FsPath(dirPath.toString(), /*isDirectory*/isDir, lastComponent);
    }

}