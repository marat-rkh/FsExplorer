package fs.explorer.providers.dirtree.archives;

import fs.explorer.providers.dirtree.path.ArchiveEntryPath;
import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.path.TargetType;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ZipArchiveTest {
    private ZipArchive zipArchive;
    private FsPath archivePath;

    @Test
    public void listsRootWithOneTopLevelDirectory() throws IOException, URISyntaxException {
        setUpHomeZip();
        List<ArchiveEntryPath> entries = zipArchive.listRoot();
        assertNotNull(entries);
        assertThat(entries, containsInAnyOrder(
                dirPath(archivePath, "home/", "home")
        ));
    }

    @Test
    public void listsRootOfEmptyZip() throws IOException, URISyntaxException {
        setUpEmptyZip();
        List<ArchiveEntryPath> entries = zipArchive.listRoot();
        assertNotNull(entries);
        assertEquals(0, entries.size());
    }

    @Test
    public void listsRootWithMultipleTopLevelEntries() throws IOException, URISyntaxException {
        setUpInsideHomeZip();
        List<ArchiveEntryPath> entries = zipArchive.listRoot();
        assertNotNull(entries);
        assertThat(entries, containsInAnyOrder(
                dirPath(archivePath, "pics/", "pics"),
                dirPath(archivePath, "music/", "music"),
                dirPath(archivePath, "documents/", "documents"),
                filePath(archivePath, "draft.txt", "draft.txt"),
                filePath(archivePath, "my-text.txt", "my-text.txt")
        ));
    }

    @Test
    public void listsEntries1() throws IOException, URISyntaxException {
        setUpHomeZip();
        ArchiveEntryPath entryPath = dirPath(archivePath, "home/", "home");
        List<ArchiveEntryPath> entries = zipArchive.list(entryPath);
        assertNotNull(entries);
        assertThat(entries, containsInAnyOrder(
                dirPath(archivePath, "home/pics/", "pics"),
                dirPath(archivePath, "home/music/", "music"),
                dirPath(archivePath, "home/documents/", "documents"),
                filePath(archivePath, "home/draft.txt", "draft.txt"),
                filePath(archivePath, "home/my-text.txt", "my-text.txt")
        ));
    }

    @Test
    public void listsEntries2() throws IOException, URISyntaxException {
        setUpHomeZip();
        ArchiveEntryPath entryPath = dirPath(archivePath, "home/music/", "music");
        List<ArchiveEntryPath> entries = zipArchive.list(entryPath);
        assertNotNull(entries);
        assertThat(entries, containsInAnyOrder(
                filePath(archivePath, "home/music/track1.mp3", "track1.mp3"),
                filePath(archivePath, "home/music/track2.mp3", "track2.mp3")
        ));
    }

    @Test
    public void listsEntries3() throws IOException, URISyntaxException {
        setUpHomeZip();
        ArchiveEntryPath entryPath = dirPath(archivePath, "home/documents/books/", "books");
        List<ArchiveEntryPath> entries = zipArchive.list(entryPath);
        assertNotNull(entries);
        assertThat(entries, containsInAnyOrder(
                filePath(archivePath, "home/documents/books/the-book.pdf", "the-book.pdf")
        ));
    }

    @Test
    public void doesNotListEntriesOfOtherArchive() throws IOException, URISyntaxException {
        setUpHomeZip();
        FsPath otherPath = testZipPath("/zips/inside-home.zip", "inside-home.zip");
        ArchiveEntryPath entryPath = dirPath(otherPath, "pics/", "pics");
        List<ArchiveEntryPath> entries = zipArchive.list(entryPath);
        assertNull(entries);
    }

    @Test
    public void doesNotListForNonExistingEntry() throws IOException, URISyntaxException {
        setUpHomeZip();
        ArchiveEntryPath entryPath = dirPath(archivePath, "home/wrong/path", "path");
        List<ArchiveEntryPath> entries = zipArchive.list(entryPath);
        assertNull(entries);
    }

    @Test
    public void listsNoEntriesForFile() throws IOException, URISyntaxException {
        setUpHomeZip();
        ArchiveEntryPath entryPath = dirPath(archivePath, "home/draft.txt", "draft.txt");
        List<ArchiveEntryPath> entries = zipArchive.list(entryPath);
        assertNotNull(entries);
        assertEquals(0, entries.size());
    }

    private void setUpHomeZip() throws URISyntaxException, IOException {
        archivePath = testZipPath("/zips/home.zip", "home.zip");
        zipArchive = new ZipArchive(archivePath, TestUtils.readZipEntries(archivePath));
    }

    private void setUpEmptyZip() throws URISyntaxException, IOException {
        archivePath = testZipPath("/zips/empty.zip", "empty.zip");
        zipArchive = new ZipArchive(archivePath, TestUtils.readZipEntries(archivePath));
    }

    private void setUpInsideHomeZip() throws URISyntaxException, IOException {
        archivePath = testZipPath("/zips/inside-home.zip", "inside-home.zip");
        zipArchive = new ZipArchive(archivePath, TestUtils.readZipEntries(archivePath));
    }

    private FsPath testZipPath(
            String relativePath, String lastComponent) throws URISyntaxException {
        Path dirPath = Paths.get(getClass().getResource(relativePath).toURI());
        return new FsPath(dirPath.toString(), TargetType.ZIP_ARCHIVE, lastComponent);
    }

    private ArchiveEntryPath dirPath(FsPath archivePath, String entryPath, String lastComponent) {
        return new ArchiveEntryPath(archivePath, entryPath, TargetType.DIRECTORY, lastComponent);
    }

    private ArchiveEntryPath filePath(FsPath archivePath, String entryPath, String lastComponent) {
        return new ArchiveEntryPath(archivePath, entryPath, TargetType.FILE, lastComponent);
    }
}