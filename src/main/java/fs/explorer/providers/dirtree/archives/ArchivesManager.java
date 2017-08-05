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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// @ThreadSafe
public class ArchivesManager implements Disposable {
    private final ConcurrentMap<FsPath, ArchiveData> archives;
    private final ArchivesReader archivesReader;
    private final Path archiveCacheDirectory;

    private static final String ARCHIVE_CACHE_PREFIX = "ArchiveCache";
    private static final String EXTRACTED_SUB_ARCHIVE_PREFIX = "SubArchive";

    public ArchivesManager(ArchivesReader archivesReader) throws IOException {
        this.archives = new ConcurrentHashMap<>();
        this.archivesReader = archivesReader;
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
        try {
            ZipArchive zipArchive = null;
            if(isTopLevel) {
                // only fsManager knows where to find top level archives
                zipArchive = archivesReader.readEntries(archivePath, fsManager);
            } else {
                // we extracted this archive so we know it is on local FS
                zipArchive = archivesReader.readEntries(archivePath);
            }
            return new ArchiveData(zipArchive, isTopLevel);
        } catch (IOException e) {
            return null;
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
        String entryName = entryPath.getEntryPath();
        FsPath destinationPath = FsPath.fromPath(subArchiveFile);

        boolean entryFound = false;
        if(archiveData.isTopLevel()) {
            // only fsManager knows where to find top level archives
            entryFound = archivesReader.extractEntryFile(
                    archivePath, entryName, destinationPath, fsManager);
        } else {
            // we extracted this archive so we know it is on local FS
            entryFound = archivesReader.extractEntryFile(
                    archivePath, entryName, destinationPath);
        }
        if(!entryFound) {
            throw new IOException("zip entry not found - " + entryPath);
        }
        return destinationPath;
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
