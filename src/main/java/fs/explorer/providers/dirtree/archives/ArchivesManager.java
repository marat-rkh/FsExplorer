package fs.explorer.providers.dirtree.archives;

import fs.explorer.providers.dirtree.FsManager;
import fs.explorer.providers.dirtree.path.ArchiveEntryPath;
import fs.explorer.providers.dirtree.path.FsPath;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ArchivesManager {
    public void addArchive(FsPath archivePath, FsManager fsManager) {

    }

    public boolean containsArchive(FsPath archivePath) {
        return false;
    }

    public void addArchiveIfAbsent(FsPath archivePath, FsManager fsManager) {

    }

    public List<ArchiveEntryPath> listArchive(FsPath path) throws IOException {
        return Collections.emptyList();
    }

    public List<ArchiveEntryPath> listSubEntry(ArchiveEntryPath path) throws IOException {
        return Collections.emptyList();
    }
}
