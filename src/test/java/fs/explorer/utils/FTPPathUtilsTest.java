package fs.explorer.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class FTPPathUtilsTest {
    @Test
    public void appendsToSingleSlash() throws Exception {
        String res = FTPPathUtils.append("/", "some/path");
        assertEquals("/some/path", res);
    }

    @Test
    public void appendsToSingleSlashWhenChildStartsWithSlash() throws Exception {
        String res = FTPPathUtils.append("/", "/some/path");
        assertEquals("/some/path", res);
    }

    @Test
    public void appendsToOneElementParent() throws Exception {
        String res = FTPPathUtils.append("/parent", "some/path");
        assertEquals("/parent/some/path", res);
    }

    @Test
    public void appendsToParentWithTrailingSlash() throws Exception {
        String res = FTPPathUtils.append("/parent/", "some/path");
        assertEquals("/parent/some/path", res);
    }

    @Test
    public void appendsToParentWhenChildStartsWithSlash() throws Exception {
        String res = FTPPathUtils.append("/parent", "/some/path");
        assertEquals("/parent/some/path", res);
    }

    @Test
    public void appendsToParentWhenChildAndParentHaveSlashes() throws Exception {
        String res = FTPPathUtils.append("/parent/", "/some/path");
        assertEquals("/parent/some/path", res);
    }

    @Test
    public void appendsToEmptyParent1() throws Exception {
        String res = FTPPathUtils.append("", "/some/path");
        assertEquals("/some/path", res);
    }

    @Test
    public void appendsToEmptyParent2() throws Exception {
        String res = FTPPathUtils.append("", "some/path");
        assertEquals("/some/path", res);
    }

    @Test
    public void appendsEmptyChild1() throws Exception {
        String res = FTPPathUtils.append("/parent", "");
        assertEquals("/parent", res);
    }

    @Test
    public void appendsEmptyChild2() throws Exception {
        String res = FTPPathUtils.append("/parent/", "");
        assertEquals("/parent", res);
    }
}