package fs.explorer.providers.dirtree.archives;

import fs.explorer.providers.dirtree.FsManager;
import fs.explorer.providers.dirtree.path.FsPath;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

// @ThreadSafe
public class ArchivesReader {
    private static final int BUFFER_SIZE = 8192;

    public ZipArchive readEntries(FsPath archivePath) throws IOException {
        return readEntries(archivePath, null);
    }

    public ZipArchive readEntries(
            FsPath archivePath, FsManager fsManager) throws IOException {
        try (
                ZipInputStream zis = openArchiveStream(archivePath, fsManager)
        ) {
            List<ZipEntry> zipEntries = new ArrayList<>();
            ZipEntry entry = null;
            while((entry = zis.getNextEntry()) != null) {
                zipEntries.add(entry);
                if(Thread.currentThread().isInterrupted()) {
                    throw new InterruptedIOException();
                }
            }
            return new ZipArchive(archivePath, zipEntries);
        } catch (IllegalArgumentException e) {
            // IllegalArgumentException exception is thrown
            // when zip entries have non default encoding we use here
            // TODO add custom encodings support
            throw new IOException("failed to decode zip file");
        }
    }

    public boolean extractEntryFile(
            FsPath archivePath,
            String entryName,
            FsPath destinationPath
    ) throws IOException {
        return extractEntryFile(archivePath, entryName, destinationPath, null);
    }

    public boolean extractEntryFile(
            FsPath archivePath,
            String entryName,
            FsPath destinationPath,
            FsManager fsManager
    ) throws IOException {
        try(
                FileOutputStream fos = new FileOutputStream(destinationPath.getPath())
        ) {
            return readEntryFile(archivePath, entryName, fos, fsManager);
        }
    }

    public boolean readEntryFile(
            FsPath archivePath,
            String entryName,
            OutputStream destination
    ) throws IOException {
        return readEntryFile(archivePath, entryName, destination, null);
    }

    public boolean readEntryFile(
            FsPath archivePath,
            String entryName,
            OutputStream destination,
            FsManager fsManager
    ) throws IOException {
        try(
                ZipInputStream zis = openArchiveStream(archivePath, fsManager);
        ) {
            boolean entryFound = false;
            ZipEntry zipEntry = null;
            try {
                while((zipEntry = zis.getNextEntry()) != null) {
                    if(zipEntry.getName().equals(entryName)) {
                        if(zipEntry.isDirectory()) {
                            throw new IOException("failed to extract entry directory");
                        }
                        entryFound = true;
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int len;
                        while((len = zis.read(buffer)) != -1) {
                            destination.write(buffer, 0, len);
                            if(Thread.currentThread().isInterrupted()) {
                                throw new InterruptedIOException();
                            }
                        }
                        break;
                    }
                    if(Thread.currentThread().isInterrupted()) {
                        throw new InterruptedIOException();
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
    }

    private ZipInputStream openArchiveStream(
            FsPath archivePath,
            FsManager fsManager
    ) throws IOException {
        if(fsManager != null) {
            byte[] contents = fsManager.readFile(archivePath);
            ByteArrayInputStream bais = new ByteArrayInputStream(contents);
            return new ZipInputStream(bais);
        } else {
            FileInputStream fis = new FileInputStream(archivePath.getPath());
            BufferedInputStream bis = new BufferedInputStream(fis);
            return new ZipInputStream(bis);
        }
    }
}
