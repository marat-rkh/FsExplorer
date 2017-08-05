package fs.explorer.providers.dirtree.archives;

import fs.explorer.providers.dirtree.LocalFsManager;
import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.path.TargetType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ArchivesReaderTest {
    private ArchivesReader archivesReader = new ArchivesReader();

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    @Test
    public void readsEntries() throws URISyntaxException, IOException {
        ZipArchive archive =
                archivesReader.readEntries(testZipPath("/zips/home.zip", "home.zip"));
        assertNotNull(archive);
        List<ZipEntryData> data = archive.listAllEntries().stream()
                .map(e -> new ZipEntryData(e.getName(), e.isDirectory()))
                .collect(Collectors.toList());
        assertThat(data, containsInAnyOrder(
                new ZipEntryData("home/", /*isDir*/true),
                new ZipEntryData("home/documents/", /*isDir*/true),
                new ZipEntryData("home/documents/books/", /*isDir*/true),
                new ZipEntryData("home/documents/books/the-book.pdf", /*isDir*/false),
                new ZipEntryData("home/music/", /*isDir*/true),
                new ZipEntryData("home/music/track1.mp3", /*isDir*/false),
                new ZipEntryData("home/music/track2.mp3", /*isDir*/false),
                new ZipEntryData("home/pics/", /*isDir*/true),
                new ZipEntryData("home/pics/photo.jpg", /*isDir*/false),
                new ZipEntryData("home/draft.txt", /*isDir*/false),
                new ZipEntryData("home/my-text.txt", /*isDir*/false)
        ));
    }

    @Test
    public void readsEntriesOfEmptyZip() throws URISyntaxException, IOException {
        ZipArchive archive =
                archivesReader.readEntries(testZipPath("/zips/empty.zip", "empty.zip"));
        assertNotNull(archive);
        assertEquals(0, archive.listAllEntries().size());
    }

    // TODO this should throw exception instead
    // Current behaviour is due to ZipInputStream
    // that cannot detect corrupted zip files
    @Test
    public void readsCorruptedZipFilesAsEmpty() throws URISyntaxException, IOException {
        ZipArchive archive =
                archivesReader.readEntries(testZipPath("/zips/bad.zip", "bad.zip"));
        assertNotNull(archive);
        assertEquals(0, archive.listAllEntries().size());
    }

    // TODO this should throw exception instead
    // Current behaviour is due to ZipInputStream
    // that cannot detect corrupted zip files
    @Test
    public void readsNonZipFilesAsEmpty() throws URISyntaxException, IOException {
        FsPath path = testDataPath("/testdirs/home/draft.txt", TargetType.FILE, "draft.txt");
        ZipArchive archive =
            archivesReader.readEntries(path);
        assertNotNull(archive);
        assertEquals(0, archive.listAllEntries().size());
    }

    @Test(expected = IOException.class)
    public void failsToReadEntriesOfDirectory() throws URISyntaxException, IOException {
        FsPath path = testDataPath("/testdirs/home", TargetType.DIRECTORY, "home");
        archivesReader.readEntries(path);
    }

    @Test(expected = IOException.class)
    public void failsToReadNonExistingFile() throws URISyntaxException, IOException {
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
        verify(fsManager).readFile(archivePath);
    }

    @Test
    public void extractsEntryFile() throws URISyntaxException, IOException {
        FsPath archive = testZipPath("/zips/home.zip", "home.zip");
        FsPath extracted = tmpDestinationFile("extracted-draft.txt");
        boolean entryFound =
                archivesReader.extractEntryFile(archive, "home/draft.txt", extracted);
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
        boolean entryFound =
                archivesReader.extractEntryFile(archive, "nested/dir1.zip", extracted);
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
        boolean entryFound =
                archivesReader.extractEntryFile(archive, "home/no-such-file.txt", extracted);
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
        verify(fsManager).readFile(archive);
    }

    private FsPath testZipPath(
            String relativePath, String lastComponent) throws URISyntaxException {
        return testDataPath(relativePath, TargetType.ZIP_ARCHIVE, lastComponent);
    }

    private FsPath testDataPath(
            String relativePath,
            TargetType targetType,
            String lastComponent
    ) throws URISyntaxException {
        Path dirPath = Paths.get(getClass().getResource(relativePath).toURI());
        return new FsPath(dirPath.toString(), targetType, lastComponent);
    }

    private FsPath tmpDestinationFile(String name) throws URISyntaxException {
        Path path = Paths.get(tmpDir.getRoot().toString(), name);
        return new FsPath(path.toString(), TargetType.FILE, name);
    }

    private static class ZipEntryData {
        private final String name;
        private final boolean isDir;

        private ZipEntryData(String name, boolean isDir) {
            this.name = name;
            this.isDir = isDir;
        }

        public String getName() { return name; }

        public boolean isDir() { return isDir; }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) {
                return true;
            }
            if(!(obj instanceof ZipEntryData)) {
                return false;
            }
            ZipEntryData other = (ZipEntryData) obj;
            return Objects.equals(name, other.name) &&
                    isDir == other.isDir;
        }

        @Override
        public int hashCode() {
            int res = 17;
            res = 31 * res + Objects.hashCode(name);
            res = 31 * res + Boolean.hashCode(isDir);
            return res;
        }
    }
}