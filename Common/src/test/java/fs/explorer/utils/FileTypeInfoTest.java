package fs.explorer.utils;

import fs.explorer.utils.FileTypeInfo;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileTypeInfoTest {
    @Test
    public void getsExtensionOfUsualFile() {
        assertEquals("txt", FileTypeInfo.getExtension("/some/dir/file.txt"));
        assertEquals("txt", FileTypeInfo.getExtension("c:\\some\\dir\\file.txt"));
    }

    @Test
    public void getsExtensionOfFileWithoutDir() {
        assertEquals("txt", FileTypeInfo.getExtension("file.txt"));
    }

    @Test
    public void alwaysGetsExtensionInLowerCase() {
        assertEquals("jpg", FileTypeInfo.getExtension("file.JPG"));
    }

    @Test
    public void returnsEmptyStringAsExtensionForNull() {
        assertEquals("", FileTypeInfo.getExtension(null));
    }

    @Test
    public void returnsEmptyStringWhenNoExtension() {
        assertEquals("", FileTypeInfo.getExtension("/some/dir/file"));
        assertEquals("", FileTypeInfo.getExtension("c:\\some\\dir\\file"));
    }

    @Test
    public void returnsEmptyStringAsExtensionOnUnixHiddenFile1() {
        assertEquals("", FileTypeInfo.getExtension("/some/dir/.file"));
    }

    @Test
    public void returnsEmptyStringAsExtensionOnHiddenFile2() {
        assertEquals("", FileTypeInfo.getExtension(".file"));
    }

    @Test
    public void handlesDirsWithDotsInNames() {
        assertEquals("ext", FileTypeInfo.getExtension("/www.some.org/dir.git/file.ext"));
        assertEquals("ext", FileTypeInfo.getExtension("c:\\www.some.org\\dir.git\\file.ext"));
    }

    @Test
    public void doesNotHandleMultipartExtensions() {
        assertEquals("gz", FileTypeInfo.getExtension("/some/dir/file.tar.gz"));
        assertEquals("gz", FileTypeInfo.getExtension("c:\\some\\dir\\file.tar.gz"));
    }

    @Test
    public void detectsTextFiles() {
        assertTrue(FileTypeInfo.isTextFile("/some/dir/file.txt"));
        assertTrue(FileTypeInfo.isTextFile("/some/dir/file.java"));
        assertTrue(FileTypeInfo.isTextFile("/some/dir/file.HTML"));
        assertTrue(FileTypeInfo.isTextFile("/some/dir/file.json"));

        assertTrue(FileTypeInfo.isTextFile("c:\\some\\dir\\file.txt"));
        assertTrue(FileTypeInfo.isTextFile("c:\\some\\dir\\file.java"));
        assertTrue(FileTypeInfo.isTextFile("c:\\some\\dir\\file.HTML"));
        assertTrue(FileTypeInfo.isTextFile("c:\\some\\dir\\file.json"));
    }

    @Test
    public void detectsImageFiles() {
        assertTrue(FileTypeInfo.isImageFile("/some/dir/file.jpeg"));
        assertTrue(FileTypeInfo.isImageFile("/some/dir/file.JPG"));
        assertTrue(FileTypeInfo.isImageFile("/some/dir/file.PNG"));
        assertTrue(FileTypeInfo.isImageFile("/some/dir/file.gif"));

        assertTrue(FileTypeInfo.isImageFile("c:\\some\\dir\\file.jpeg"));
        assertTrue(FileTypeInfo.isImageFile("c:\\some\\dir\\file.JPG"));
        assertTrue(FileTypeInfo.isImageFile("c:\\some\\dir\\file.PNG"));
        assertTrue(FileTypeInfo.isImageFile("c:\\some\\dir\\file.gif"));
    }
}