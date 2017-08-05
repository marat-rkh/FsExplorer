package fs.explorer.providers.dirtree.archives;

import fs.explorer.providers.dirtree.FsManager;
import fs.explorer.providers.dirtree.path.ArchiveEntryPath;
import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.path.TargetType;
import fs.explorer.utils.Disposable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

// @ThreadSafe
public class ArchivesManager implements Disposable {
    private final ConcurrentMap<FsPath, ArchiveData> archives = new ConcurrentHashMap<>();
    private final Path archiveCacheDirectory;

    private static final String ARCHIVE_CACHE_PREFIX = "ArchiveCache";
    private static final String EXTRACTED_SUB_ARCHIVE_PREFIX = "SubArchive";

    public ArchivesManager() throws IOException {
        this.archiveCacheDirectory = Files.createTempDirectory(ARCHIVE_CACHE_PREFIX);
    }

    public ZipArchive addArchiveIfAbsent(
            FsPath archivePath, FsManager fsManager) throws IOException {
        ArchiveData archiveData = addArchiveIfAbsent(
                archivePath, /*isTopLevel*/true, fsManager);
        return archiveData.getZipArchive();
    }

    public boolean containsArchive(FsPath archivePath) {
        return archives.containsKey(archivePath);
    }

    public List<ArchiveEntryPath> listArchive(
            FsPath archivePath, FsManager fsManager) throws IOException {
        ArchiveData archiveData = archives.get(archivePath);
        if (archiveData == null) {
            return null;
        }
        return archiveData.getZipArchive().listRoot();
    }

    public List<ArchiveEntryPath> listSubEntry(
            ArchiveEntryPath entryPath, FsManager fsManager) throws IOException {
        ArchiveData archiveData = archives.get(entryPath.getArchivePath());
        if(archiveData == null) {
            return null;
        }
        TargetType entryType = entryPath.getTargetType();
        if(entryType == TargetType.DIRECTORY) {
            return archiveData.getZipArchive().list(entryPath);
        } else if(entryType == TargetType.ZIP_ARCHIVE) {
            FsPath subArchivePath = extractSubArchive(archiveData, entryPath, fsManager);
            ArchiveData subArchiveData = addArchiveIfAbsent(
                    subArchivePath, /*isTopLevel*/false, fsManager);
            return subArchiveData.getZipArchive().listRoot();
        } else {
            return null;
        }
    }

    public void clearCache() {
        // TODO delete archiveCacheDirectory
    }

    @Override
    public void dispose() {
        clearCache();
    }

    private ArchiveData addArchiveIfAbsent(
            FsPath archivePath,
            boolean isTopLevel,
            FsManager fsManager
    ) throws IOException {
        ArchiveData archiveData = archives.computeIfAbsent(
                archivePath, key -> tryMakeArchive(key, isTopLevel, fsManager));
        if(archiveData == null) {
            throw new IOException("failed to process archive");
        }
        return archiveData;
    }

    private ArchiveData tryMakeArchive(
            FsPath archivePath, boolean isTopLevel, FsManager fsManager) {
        try (
                ZipInputStream zis = openArchiveStream(archivePath, isTopLevel, fsManager)
        ) {
            List<ZipEntry> zipEntries = new ArrayList<>();
            ZipEntry entry = null;
            while((entry = zis.getNextEntry()) != null) {
                zipEntries.add(entry);
            }
            return new ArchiveData(new ZipArchive(archivePath, zipEntries), isTopLevel);
        } catch (IOException | IllegalArgumentException e) {
            // IllegalArgumentException exception is thrown
            // when zip entries have non default encoding we use here
            // TODO add custom encodings support
            return null;
        }
    }

    private ZipInputStream openArchiveStream(
            FsPath archivePath,
            boolean isTopLevel,
            FsManager fsManager
    ) throws IOException {
        if(isTopLevel) {
            // only fsManager knows where to find top level archives
            byte[] contents = fsManager.readFile(archivePath);
            ByteArrayInputStream bais = new ByteArrayInputStream(contents);
            return new ZipInputStream(bais);
        } else {
            // we extracted this archive so we know it is on local FS
            FileInputStream fis = new FileInputStream(archivePath.getPath());
            BufferedInputStream bis = new BufferedInputStream(fis);
            return new ZipInputStream(bis);
        }
    }

    private FsPath extractSubArchive(
            ArchiveData archiveData,
            ArchiveEntryPath entryPath,
            FsManager fsManager
    ) throws IOException {
        Path subArchiveDirectory = Files.createTempDirectory(
                archiveCacheDirectory, EXTRACTED_SUB_ARCHIVE_PREFIX);
        Path subArchiveFile = Paths.get(
                subArchiveDirectory.toString(), entryPath.getLastComponent());
        FsPath archivePath = entryPath.getArchivePath();
        try(
                ZipInputStream zis =
                        openArchiveStream(archivePath, archiveData.isTopLevel(), fsManager);
                FileOutputStream fos = new FileOutputStream(subArchiveFile.toFile())
        ) {
            boolean entryFound = extractZipEntry(entryPath.getEntryPath(), zis, fos);
            if(!entryFound) {
                String message = "zip entry not found, archive " +
                        entryPath.getArchivePath().getPath() + ", entry " +
                        entryPath.getEntryPath();
                throw new IOException(message);
            }
            return FsPath.fromPath(subArchiveFile);
        }
    }

    private boolean extractZipEntry(
            String entryName,
            ZipInputStream zis,
            FileOutputStream fos
    ) throws IOException {
        boolean entryFound = false;
        ZipEntry zipEntry = null;
        try {
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (zipEntry.getName().equals(entryName)) {
                    entryFound = true;
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = zis.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    break;
                }
            }
        } catch (IllegalArgumentException e) {
            // IllegalArgumentException exception is thrown
            // when zip entries have non default encoding we use here
            // TODO add custom encodings support
            throw new IOException("failed to decode zip file");
        }
        return entryFound;
    }

    private static class ArchiveData {
        private final ZipArchive zipArchive;
        private final boolean isTopLevel;

        private ArchiveData(ZipArchive zipArchive, boolean isTopLevel) {
            this.zipArchive = zipArchive;
            this.isTopLevel = isTopLevel;
        }

        public ZipArchive getZipArchive() { return zipArchive; }

        public boolean isTopLevel() { return isTopLevel; }
    }
}
