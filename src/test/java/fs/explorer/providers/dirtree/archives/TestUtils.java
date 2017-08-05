package fs.explorer.providers.dirtree.archives;

import fs.explorer.providers.dirtree.path.FsPath;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
}
