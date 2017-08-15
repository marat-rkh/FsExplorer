package fs.explorer.providers.dirtree.archives;

import fs.explorer.providers.dirtree.local.LocalFsManager;
import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.path.TargetType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static fs.explorer.providers.dirtree.archives.TestUtils.ZipEntryData;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ArchivesReaderTest {
    private ArchivesReader archivesReader = new ArchivesReader();

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    @Test
    public void readsEntries() throws URISyntaxException, IOException {
        ZipArchive archive = archivesReader.readEntries(testZipPath("/zips/home.zip", "home.zip"));
        assertNotNull(archive);
        List<TestUtils.ZipEntryData> data = archive.listAllEntries().stream()
                .map(e -> new ZipEntryData(e.getName(), e.isDirectory()))
                .collect(Collectors.toList());
        assertThat(data, containsInAnyOrder(
                new ZipEntryData("home/", true),
                new ZipEntryData("home/documents/", true),
                new ZipEntryData("home/documents/books/", true),
                new ZipEntryData("home/documents/books/the-book.pdf", false),
                new ZipEntryData("home/music/", true),
                new ZipEntryData("home/music/track1.mp3", false),
                new ZipEntryData("home/music/track2.mp3", false),
                new ZipEntryData("home/pics/", true),
                new ZipEntryData("home/pics/photo.jpg", false),
                new ZipEntryData("home/draft.txt", false),
                new ZipEntryData("home/my-text.txt", false)
        ));
    }

    @Test
    public void readsEntriesOfEmptyZip() throws URISyntaxException, IOException {
        ZipArchive archive = archivesReader.readEntries(testZipPath("/zips/empty.zip", "empty.zip"));
        assertNotNull(archive);
        assertEquals(0, archive.listAllEntries().size());
    }

    // TODO this should throw exception instead
    // Current behaviour is due to ZipInputStream
    // that cannot detect corrupted zip files
    @Test
    public void readsEntriesOfCorruptedZipFilesAsEmpty() throws URISyntaxException, IOException {
        ZipArchive archive = archivesReader.readEntries(testZipPath("/zips/bad.zip", "bad.zip"));
        assertNotNull(archive);
        assertEquals(0, archive.listAllEntries().size());
    }

    // TODO this should throw exception instead
    // Current behaviour is due to ZipInputStream
    // that cannot detect corrupted zip files
    @Test
    public void readsEntriesOfNonZipFilesAsEmpty() throws URISyntaxException, IOException {
        FsPath path = testDataPath("/testdirs/home/draft.txt", TargetType.FILE, "draft.txt");
        ZipArchive archive = archivesReader.readEntries(path);
        assertNotNull(archive);
        assertEquals(0, archive.listAllEntries().size());
    }

    @Test(expected = IOException.class)
    public void failsToReadEntriesOfDirectory() throws URISyntaxException, IOException {
        FsPath path = testDataPath("/testdirs/home", TargetType.DIRECTORY, "home");
        archivesReader.readEntries(path);
    }

    @Test(expected = IOException.class)
    public void failsToReadEntriesOfNonExistingFile() throws URISyntaxException, IOException {
        FsPath fsPath = new FsPath("/zips/no-such.zip", TargetType.ZIP_ARCHIVE, "no-such.zip");
        archivesReader.readEntries(fsPath);
    }

    @Test
    public void readsEntriesViaFsManager() throws URISyntaxException, IOException {
        LocalFsManager fsManager = spy(new LocalFsManager());
        FsPath archivePath = testZipPath("/zips/home.zip", "home.zip");
        ZipArchive archive = archivesReader.readEntries(archivePath, fsManager);
        assertNotNull(archive);
        assertEquals(11, archive.listAllEntries().size());
        verify(fsManager).withFileStream(same(archivePath), any());
    }

    @Test
    public void extractsEntryFile() throws URISyntaxException, IOException {
        FsPath archive = testZipPath("/zips/home.zip", "home.zip");
        FsPath extracted = tmpDestinationFile("extracted-draft.txt");
        boolean entryFound = archivesReader.extractEntryFile(archive, "home/draft.txt", extracted);
        assertTrue(entryFound);
        Path extractedPath = Paths.get(extracted.getPath());
        assertTrue(Files.exists(extractedPath));
        List<String> lines = Files.readAllLines(extractedPath);
        assertEquals(1, lines.size());
        assertEquals("draft text", lines.get(0));
    }

    @Test
    public void extractsEntryZip() throws URISyntaxException, IOException {
        FsPath archive = testZipPath("/zips/nested.zip", "nested.zip");
        FsPath extracted = tmpDestinationFile("extracted-dir1.zip");
        boolean entryFound = archivesReader.extractEntryFile(archive, "nested/dir1.zip", extracted);
        assertTrue(entryFound);
        Path extractedPath = Paths.get(extracted.getPath());
        assertTrue(Files.exists(extractedPath));
        ZipArchive extractedArchive = archivesReader.readEntries(extracted);
        assertNotNull(extractedArchive);
        assertEquals(2, extractedArchive.listAllEntries().size());
    }

    @Test(expected = IOException.class)
    public void failsToExtractsEntryDirectory() throws URISyntaxException, IOException {
        FsPath archive = testZipPath("/zips/home.zip", "home.zip");
        FsPath extracted = tmpDestinationFile("extracted-home");
        archivesReader.extractEntryFile(archive, "home/", extracted);
    }

    @Test
    public void doesNotExtractNonExistingEntry() throws URISyntaxException, IOException {
        FsPath archive = testZipPath("/zips/home.zip", "home.zip");
        FsPath extracted = tmpDestinationFile("extracted");
        boolean entryFound = archivesReader.extractEntryFile(
                archive, "home/no-such-file.txt", extracted);
        assertFalse(entryFound);
    }

    @Test(expected = IOException.class)
    public void failsToExtractsFromNonExistingArchive() throws URISyntaxException, IOException {
        FsPath archive = new FsPath("/zips/no-such.zip", TargetType.ZIP_ARCHIVE, "no-such.zip");
        FsPath extracted = tmpDestinationFile("extracted");
        archivesReader.extractEntryFile(archive, "dir/", extracted);
    }

    @Test
    public void extractsEntryFileViaFsManager() throws URISyntaxException, IOException {
        FsPath archive = testZipPath("/zips/home.zip", "home.zip");
        FsPath extracted = tmpDestinationFile("extracted-draft.txt");
        LocalFsManager fsManager = spy(new LocalFsManager());
        boolean entryFound = archivesReader.extractEntryFile(
                archive, "home/draft.txt", extracted, fsManager);
        assertTrue(entryFound);
        Path extractedPath = Paths.get(extracted.getPath());
        assertTrue(Files.exists(extractedPath));
        List<String> lines = Files.readAllLines(extractedPath);
        assertEquals(1, lines.size());
        assertEquals("draft text", lines.get(0));
        verify(fsManager).withFileStream(same(archive), any());
    }

    @Test
    public void readsEntryFile1() throws URISyntaxException, IOException {
        FsPath archive = testZipPath("/zips/data.zip", "data.zip");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean entryFound = archivesReader.readEntryFile(archive, "text/descr.txt", baos);
        assertTrue(entryFound);
        String contents = new String(baos.toByteArray());
        assertEquals("textual description", contents);
    }

    @Test
    public void readsEntryFile2() throws URISyntaxException, IOException {
        FsPath archive = testZipPath("/zips/data.zip", "data.zip");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean entryFound = archivesReader.readEntryFile(archive, "drunk.jpg", baos);
        assertTrue(entryFound);
        assertTrue(baos.toByteArray().length > 0);
    }

    @Test
    public void readsEntryFileViaFsManager() throws URISyntaxException, IOException {
        FsPath archive = testZipPath("/zips/data.zip", "data.zip");
        LocalFsManager fsManager = spy(new LocalFsManager());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean entryFound = archivesReader.readEntryFile(
                archive, "text/descr.txt", baos, fsManager);
        assertTrue(entryFound);
        String contents = new String(baos.toByteArray());
        assertEquals("textual description", contents);
        verify(fsManager).withFileStream(same(archive), any());
    }

    private FsPath testZipPath(String relativePath, String lastComponent) throws URISyntaxException {
        return testDataPath(relativePath, TargetType.ZIP_ARCHIVE, lastComponent);
    }

    private FsPath testDataPath(String relativePath, TargetType targetType, String lastComponent)
            throws URISyntaxException {
        Path dirPath = Paths.get(getClass().getResource(relativePath).toURI());
        return new FsPath(dirPath.toString(), targetType, lastComponent);
    }

    private FsPath tmpDestinationFile(String name) throws URISyntaxException {
        Path path = Paths.get(tmpDir.getRoot().toString(), name);
        return new FsPath(path.toString(), TargetType.FILE, name);
    }
}