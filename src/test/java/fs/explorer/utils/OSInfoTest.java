package fs.explorer.utils;

import fs.explorer.providers.dirtree.path.FsPath;
import org.junit.Test;

import static org.junit.Assert.*;

public class OSInfoTest {
    @Test
    public void getRootFsPath() throws Exception {
        FsPath root = OSInfo.getRootFsPath();
        assertTrue(root.getLastComponent().equals("/") ||
                root.getLastComponent().equalsIgnoreCase("C:\\"));
        assertTrue(root.getPath().equals("/") ||
                root.getPath().equalsIgnoreCase("C:\\"));
        assertTrue(root.isDirectory());
    }
}