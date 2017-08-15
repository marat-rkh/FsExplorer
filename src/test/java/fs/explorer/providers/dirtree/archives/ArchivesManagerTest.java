package fs.explorer.providers.dirtree.archives;

import fs.explorer.providers.dirtree.FsManager;
import fs.explorer.providers.dirtree.path.ArchiveEntryPath;
import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.path.TargetType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;

import static fs.explorer.providers.dirtree.archives.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ArchivesManagerTest {
    private ArchivesManager archivesManager;
    private ArchivesReader archivesReader;
    private FsManager fsManager;

    @Before
    public void setUp() throws IOException {
        archivesReader = mock(ArchivesReader.class);
        fsManager = mock(FsManager.class);
        archivesManager = new ArchivesManager(archivesReader);
    }

    @After
    public void cleanUp() throws IOException {
        archivesManager.clearCache();
    }

    @Test
    public void addsArchiveViaFsManager() throws IOException {
        FsPath fsPath = new FsPath("/some/arch.zip", TargetType.ZIP_ARCHIVE, "arch.zip");
        ZipArchive testArchive = new ZipArchive(fsPath, Collections.emptyList());
        when(archivesReader.readEntries(any(), same(fsManager))).thenReturn(testArchive);
        ZipArchive actualArchive = archivesManager.addArchiveIfAbsent(fsPath, fsManager);

        assertTrue(actualArchive == testArchive);
        assertTrue(archivesManager.containsArchive(fsPath));
        verify(archivesReader).readEntries(any(), any());
        verify(archivesReader, never()).readEntries(any());
    }

    @Test
    public void addsArchiveOnlyIfAbsent() throws IOException {
        FsPath fsPath = new FsPath("/some/arch.zip", TargetType.ZIP_ARCHIVE, "arch.zip");
        ZipArchive testArchive = new ZipArchive(fsPath, Collections.emptyList());
        ZipArchive otherArchive = new ZipArchive(fsPath, Collections.emptyList());
        when(archivesReader.readEntries(any(), same(fsManager)))
                .thenReturn(testArchive)
                .thenReturn(otherArchive);
        archivesManager.addArchiveIfAbsent(fsPath, fsManager);
        ZipArchive actualArchive = archivesManager.addArchiveIfAbsent(fsPath, fsManager);

        assertTrue(actualArchive == testArchive);
        assertTrue(archivesManager.containsArchive(fsPath));
        verify(archivesReader).readEntries(any(), any());
        verify(archivesReader, never()).readEntries(any());
    }

    @Test(expected = IOException.class)
    public void failsToAddOnNonArchivePath() throws IOException {
        FsPath fsPath = new FsPath("/some/dir", TargetType.DIRECTORY, "dir");
        archivesManager.addArchiveIfAbsent(fsPath, fsManager);
    }

    @Test(expected = IOException.class)
    public void failsToAddOnArchiveReaderFail() throws IOException {
        FsPath fsPath = new FsPath("/some/arch.zip", TargetType.ZIP_ARCHIVE, "arch.zip");
        doThrow(IOException.class).when(archivesReader).readEntries(any(), any());
        archivesManager.addArchiveIfAbsent(fsPath, fsManager);
    }

    @Test
    public void listsAddedTopLevelArchive() throws IOException {
        FsPath fsPath = new FsPath("/some/arch.zip", TargetType.ZIP_ARCHIVE, "arch.zip");
        ZipArchive testArchive = new ZipArchive(fsPath, Arrays.asList(
                new ZipEntry("home/"),
                new ZipEntry("home/draft.txt")
        ));
        when(archivesReader.readEntries(any(), same(fsManager))).thenReturn(testArchive);
        archivesManager.addArchiveIfAbsent(fsPath, fsManager);
        List<ArchiveEntryPath> entries = archivesManager.listArchive(fsPath, fsManager);

        assertNotNull(entries);
        assertThat(entries, containsInAnyOrder(
                dirPath(fsPath, "home/", "home")
        ));
    }

    @Test
    public void doesNotListNotAddedArchive() throws IOException {
        FsPath fsPath = new FsPath("/some/arch.zip", TargetType.ZIP_ARCHIVE, "arch.zip");
        ZipArchive testArchive = new ZipArchive(fsPath, Arrays.asList(
                new ZipEntry("home/"),
                new ZipEntry("home/draft.txt")
        ));
        when(archivesReader.readEntries(any(), same(fsManager))).thenReturn(testArchive);
        archivesManager.addArchiveIfAbsent(fsPath, fsManager);
        FsPath otherPath = new FsPath("/other/arch.zip", TargetType.ZIP_ARCHIVE, "arch.zip");
        List<ArchiveEntryPath> entries = archivesManager.listArchive(otherPath, fsManager);

        assertNull(entries);
    }

    @Test
    public void listsSubEntry() throws IOException {
        FsPath fsPath = new FsPath("/some/arch.zip", TargetType.ZIP_ARCHIVE, "arch.zip");
        ZipArchive testArchive = new ZipArchive(fsPath, Arrays.asList(
                new ZipEntry("home/"),
                new ZipEntry("home/draft.txt"),
                new ZipEntry("home/sub-arch.zip"),
                new ZipEntry("home/pics/"),
                new ZipEntry("home/pics/photo.jpg")
        ));
        when(archivesReader.readEntries(any(), same(fsManager))).thenReturn(testArchive);
        archivesManager.addArchiveIfAbsent(fsPath, fsManager);
        List<ArchiveEntryPath> entries = archivesManager.listSubEntry(
                dirPath(fsPath, "home/", "home"), fsManager);

        assertNotNull(entries);
        assertThat(entries, containsInAnyOrder(
                filePath(fsPath, "home/draft.txt", "draft.txt"),
                zipPath(fsPath, "home/sub-arch.zip", "sub-arch.zip"),
                dirPath(fsPath, "home/pics/", "pics")
        ));
    }

    @Test
    public void listsZipSubEntry() throws IOException {
        FsPath fsPath = new FsPath("/some/arch.zip", TargetType.ZIP_ARCHIVE, "arch.zip");
        ZipArchive testArchive = new ZipArchive(fsPath, Arrays.asList(
                new ZipEntry("home/"),
                new ZipEntry("home/draft.txt"),
                new ZipEntry("home/sub-arch.zip"),
                new ZipEntry("home/pics/"),
                new ZipEntry("home/pics/photo.jpg")
        ));
        ZipArchive subArchive = new ZipArchive(fsPath, Arrays.asList(
                new ZipEntry("sub/"),
                new ZipEntry("sub/file.txt")
        ));
        when(archivesReader.readEntries(any(), same(fsManager))).thenReturn(testArchive);
        when(archivesReader.readEntries(any())).thenReturn(subArchive);
        when(archivesReader.extractEntryFile(any(), any(), any(), any())).thenReturn(true);
        when(archivesReader.extractEntryFile(any(), any(), any())).thenReturn(false);
        archivesManager.addArchiveIfAbsent(fsPath, fsManager);
        List<ArchiveEntryPath> entries = archivesManager.listSubEntry(
                zipPath(fsPath, "home/sub-arch.zip", "sub-arch.zip"), fsManager);

        assertNotNull(entries);
        assertThat(entries, containsInAnyOrder(
                dirPath(fsPath, "sub/", "sub")
        ));

        ArgumentCaptor<FsPath> captor = ArgumentCaptor.forClass(FsPath.class);
        verify(archivesReader).readEntries(captor.capture());
        FsPath extractedSubArchivePath = captor.getValue();

        String path = extractedSubArchivePath.getPath().toLowerCase();
        String systemTmpDir = System.getProperty("java.io.tmpdir").toLowerCase();
        assertTrue(path.startsWith(systemTmpDir));
        assertTrue(path.endsWith("sub-arch.zip"));
    }

    @Test
    public void doesNotListSubEntryOnFile() throws IOException {
        FsPath fsPath = new FsPath("/some/arch.zip", TargetType.ZIP_ARCHIVE, "arch.zip");
        ZipArchive testArchive = new ZipArchive(fsPath, Arrays.asList(
                new ZipEntry("home/"),
                new ZipEntry("home/draft.txt")
        ));
        when(archivesReader.readEntries(any(), same(fsManager))).thenReturn(testArchive);
        archivesManager.addArchiveIfAbsent(fsPath, fsManager);
        List<ArchiveEntryPath> entries = archivesManager.listSubEntry(
                filePath(fsPath, "home/draft.txt", "draft.txt"), fsManager);
        assertNull(entries);
    }

    @Test
    public void doesNotListSubEntryOnAbsentArchive() throws IOException {
        FsPath fsPath = new FsPath("/some/arch.zip", TargetType.ZIP_ARCHIVE, "arch.zip");
        ZipArchive testArchive = new ZipArchive(fsPath, Arrays.asList(
                new ZipEntry("home/"),
                new ZipEntry("home/draft.txt")
        ));
        when(archivesReader.readEntries(any(), same(fsManager))).thenReturn(testArchive);
        archivesManager.addArchiveIfAbsent(fsPath, fsManager);
        FsPath otherPath = new FsPath("/other/arch.zip", TargetType.ZIP_ARCHIVE, "arch.zip");
        List<ArchiveEntryPath> entries = archivesManager.listSubEntry(
                dirPath(otherPath, "home/", "home"), fsManager);
        assertNull(entries);
    }

    @Test
    public void doesNotListSubEntryOnNonExistingPath() throws IOException {
        FsPath fsPath = new FsPath("/some/arch.zip", TargetType.ZIP_ARCHIVE, "arch.zip");
        ZipArchive testArchive = new ZipArchive(fsPath, Arrays.asList(
                new ZipEntry("home/"),
                new ZipEntry("home/draft.txt")
        ));
        when(archivesReader.readEntries(any(), same(fsManager))).thenReturn(testArchive);
        archivesManager.addArchiveIfAbsent(fsPath, fsManager);
        FsPath otherPath = new FsPath("/other/arch.zip", TargetType.ZIP_ARCHIVE, "arch.zip");
        List<ArchiveEntryPath> entries = archivesManager.listSubEntry(
                dirPath(otherPath, "data/", "data"), fsManager);
        assertNull(entries);
    }

    @Test
    public void readsEntry() throws IOException {
        FsPath archivePath = new FsPath("/some/arch.zip", TargetType.ZIP_ARCHIVE, "arch.zip");
        ZipArchive testArchive = new ZipArchive(archivePath, Collections.emptyList());
        when(archivesReader.readEntries(any(), same(fsManager))).thenReturn(testArchive);
        archivesManager.addArchiveIfAbsent(archivePath, fsManager);

        when(archivesReader.readEntryFile(any(), any(), any(), any())).thenReturn(true);
        byte[] contents = archivesManager.readEntry(filePath(archivePath, "", ""), fsManager);

        assertNotNull(contents);
    }

    @Test
    public void doesNotReadEntryWhenArchiveNotAdded() throws IOException {
        FsPath archivePath = new FsPath("/some/arch.zip", TargetType.ZIP_ARCHIVE, "arch.zip");
        ZipArchive testArchive = new ZipArchive(archivePath, Collections.emptyList());
        when(archivesReader.readEntries(any(), same(fsManager))).thenReturn(testArchive);
        archivesManager.addArchiveIfAbsent(archivePath, fsManager);

        when(archivesReader.readEntryFile(any(), any(), any(), any())).thenReturn(true);
        FsPath otherArch = new FsPath("/other/arch.zip", TargetType.ZIP_ARCHIVE, "arch.zip");
        byte[] contents = archivesManager.readEntry(filePath(otherArch, "", ""), fsManager);

        assertNull(contents);
    }

    @Test
    public void doesNotReadNonExistingEntry() throws IOException {
        FsPath archivePath = new FsPath("/some/arch.zip", TargetType.ZIP_ARCHIVE, "arch.zip");
        ZipArchive testArchive = new ZipArchive(archivePath, Collections.emptyList());
        when(archivesReader.readEntries(any(), same(fsManager))).thenReturn(testArchive);
        archivesManager.addArchiveIfAbsent(archivePath, fsManager);

        when(archivesReader.readEntryFile(any(), any(), any(), any())).thenReturn(false);
        byte[] contents = archivesManager.readEntry(filePath(archivePath, "", ""), fsManager);

        assertNull(contents);
    }
}