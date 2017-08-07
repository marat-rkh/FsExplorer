package fs.explorer.providers.dirtree.archives;

import fs.explorer.providers.dirtree.path.ArchiveEntryPath;
import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.path.TargetType;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TestUtils {
    public static List<ZipEntry> readZipEntries(FsPath testZipFilePath) throws IOException {
        try(
                FileInputStream fis = new FileInputStream(testZipFilePath.getPath());
                BufferedInputStream bis = new BufferedInputStream(fis);
                ZipInputStream zis = new ZipInputStream(bis)
        ) {
            List<ZipEntry> zipEntries = new ArrayList<>();
            ZipEntry entry = null;
            while((entry = zis.getNextEntry()) != null) {
                zipEntries.add(entry);
            }
            return zipEntries;
        }
    }

    public static ArchiveEntryPath dirPath(
            FsPath archivePath, String entryPath, String lastComponent) {
        return new ArchiveEntryPath(
                archivePath, entryPath, TargetType.DIRECTORY, lastComponent);
    }

    public static ArchiveEntryPath filePath(
            FsPath archivePath, String entryPath, String lastComponent) {
        return new ArchiveEntryPath(
                archivePath, entryPath, TargetType.FILE, lastComponent);
    }

    public static ArchiveEntryPath zipPath(
            FsPath archivePath, String entryPath, String lastComponent) {
        return new ArchiveEntryPath(
                archivePath, entryPath, TargetType.ZIP_ARCHIVE, lastComponent);
    }

    public static class ZipEntryData {
        private final String name;
        private final boolean isDir;

        public ZipEntryData(String name, boolean isDir) {
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
