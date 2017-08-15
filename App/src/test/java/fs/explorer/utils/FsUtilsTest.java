package fs.explorer.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class FsUtilsTest {
    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    @Test
    public void deletesDirectoryRecursively() throws Exception {
        Path testDir = Paths.get(getClass().getResource("/testdirs/home").toURI());
        TestUtils.copyDirectory(testDir, tmpDir.getRoot().toPath());
        Path copiedTestDir = Paths.get(tmpDir.getRoot().toString(), "/home");

        assertTrue(Files.exists(copiedTestDir));
        FsUtils.deleteDirectoryRecursively(copiedTestDir);
        assertFalse(Files.exists(copiedTestDir));
    }

    @Test(expected = IOException.class)
    public void failsToDeleteFileRecursively() throws Exception {
        Path testFile = Paths.get(getClass().getResource("/testdirs/home/draft.txt").toURI());
        Path copiedTestFile = Paths.get(tmpDir.getRoot().toString(), "/draft.txt");
        try {
            Files.copy(testFile, copiedTestFile);
        } catch (IOException e) {
            fail();
        }

        assertTrue(Files.exists(copiedTestFile));
        FsUtils.deleteDirectoryRecursively(copiedTestFile);
    }
}