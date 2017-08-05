package fs.explorer.providers.dirtree.archives;

import fs.explorer.providers.dirtree.path.ArchiveEntryPath;
import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.path.TargetType;
import fs.explorer.utils.FileTypeInfo;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

// TODO improve performance
// @ThreadSafe
public class ZipArchive {
    private final FsPath path;
    private final List<ExtZipEntry> entries;

    public ZipArchive(FsPath path, List<ZipEntry> entries) {
        this.path = path;
        this.entries = entries.stream()
                .map(ZipArchive::toExtZipEntry)
                .collect(Collectors.toList());
    }

    public List<ArchiveEntryPath> listRoot() {
        return entries.stream()
                .filter(ee -> ee.getNameComponentsCount() == 1)
                .map(this::toArchiveEntryPath)
                .collect(Collectors.toList());
    }

    public List<ArchiveEntryPath> list(ArchiveEntryPath entryPath) {
        if(!entryPath.getArchivePath().equals(path)) {
            return null;
        }
        Optional<ExtZipEntry> optExtEntry = entries.stream()
                .filter(ee -> ee.getZipEntry().getName().equals(entryPath.getEntryPath()))
                .findFirst();
        if(!optExtEntry.isPresent()) {
            return null;
        }
        ExtZipEntry extEntry = optExtEntry.get();
        String name = extEntry.getZipEntry().getName();
        int componentsCount = extEntry.getNameComponentsCount();
        return entries.stream()
                .filter(ee -> ee.getNameComponentsCount() == componentsCount + 1)
                .filter(ee -> ee.getZipEntry().getName().startsWith(name))
                .map(this::toArchiveEntryPath)
                .collect(Collectors.toList());
    }

    private static ExtZipEntry toExtZipEntry(ZipEntry entry) {
        int nameComponentsCount = Paths.get(entry.getName()).getNameCount();
        return new ExtZipEntry(entry, nameComponentsCount);
    }

    private ArchiveEntryPath toArchiveEntryPath(ExtZipEntry extEntry) {
        ZipEntry entry = extEntry.getZipEntry();
        String entryName = entry.getName();
        TargetType targetType = null;
        if(entry.isDirectory()) {
            targetType = TargetType.DIRECTORY;
        } else if(FileTypeInfo.isZipArchive(entryName)) {
            targetType = TargetType.ZIP_ARCHIVE;
        } else {
            targetType = TargetType.FILE;
        }
        String lastComponent = Paths.get(entryName).getFileName().toString();
        return new ArchiveEntryPath(path, entryName, targetType, lastComponent);
    }

    private static class ExtZipEntry {
        private final ZipEntry zipEntry;
        private final int nameComponentsCount;

        private ExtZipEntry(ZipEntry zipEntry, int nameComponentsCount) {
            this.zipEntry = zipEntry;
            this.nameComponentsCount = nameComponentsCount;
        }

        public ZipEntry getZipEntry() { return zipEntry; }

        public int getNameComponentsCount() { return nameComponentsCount; }
    }
}
