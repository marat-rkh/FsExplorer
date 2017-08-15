package fs.explorer.providers.dirtree;

import fs.explorer.providers.dirtree.path.ArchiveEntryPath;
import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.path.TargetType;
import org.junit.Test;

import static org.junit.Assert.*;

public class TreeNodeDataTest {
    @Test
    public void checksThatFsPathIsDirectory() {
        TreeNodeData data1 = new TreeNodeData("", new FsPath("", TargetType.DIRECTORY, ""));
        assertTrue(data1.pathTargetIsDirectory());

        TreeNodeData data2 = new TreeNodeData("", new FsPath("", TargetType.FILE, ""));
        assertFalse(data2.pathTargetIsDirectory());

        TreeNodeData data3 = new TreeNodeData("", new FsPath("", TargetType.ZIP_ARCHIVE, ""));
        assertFalse(data3.pathTargetIsDirectory());
    }

    @Test
    public void checksThatArchiveEntryPathIsDirectory() {
        TreeNodeData data1 = new TreeNodeData(
                "", new ArchiveEntryPath(null, "", TargetType.DIRECTORY, ""));
        assertTrue(data1.pathTargetIsDirectory());

        TreeNodeData data2 = new TreeNodeData(
                "", new ArchiveEntryPath(null, "", TargetType.FILE, ""));
        assertFalse(data2.pathTargetIsDirectory());

        TreeNodeData data3 = new TreeNodeData(
                "", new ArchiveEntryPath(null, "", TargetType.ZIP_ARCHIVE, ""));
        assertFalse(data3.pathTargetIsDirectory());
    }

    @Test
    public void returnsTargetTypeOfFsPath() {
        TreeNodeData data1 = new TreeNodeData("", new FsPath("", TargetType.DIRECTORY, ""));
        assertEquals(TargetType.DIRECTORY, data1.getPathTargetType());

        TreeNodeData data2 = new TreeNodeData("", new FsPath("", TargetType.FILE, ""));
        assertEquals(TargetType.FILE, data2.getPathTargetType());

        TreeNodeData data3 = new TreeNodeData("", new FsPath("", TargetType.ZIP_ARCHIVE, ""));
        assertEquals(TargetType.ZIP_ARCHIVE, data3.getPathTargetType());
    }

    @Test
    public void returnsTargetTypeOfArchiveEntryPath() {
        TreeNodeData data1 = new TreeNodeData(
                "", new ArchiveEntryPath(null, "", TargetType.DIRECTORY, ""));
        assertEquals(TargetType.DIRECTORY, data1.getPathTargetType());

        TreeNodeData data2 = new TreeNodeData(
                "", new ArchiveEntryPath(null, "", TargetType.FILE, ""));
        assertEquals(TargetType.FILE, data2.getPathTargetType());

        TreeNodeData data3 = new TreeNodeData(
                "", new ArchiveEntryPath(null, "", TargetType.ZIP_ARCHIVE, ""));
        assertEquals(TargetType.ZIP_ARCHIVE, data3.getPathTargetType());
    }

    @Test
    public void returnsLastComponentOfFsPath() {
        TreeNodeData data = new TreeNodeData("", new FsPath("", null, "data.txt"));
        assertEquals("data.txt", data.getPathLastComponent());
    }

    @Test
    public void returnsLastComponentOfArchiveEntryPath() {
        TreeNodeData data = new TreeNodeData("", new FsPath("", null, "data.txt"));
        assertEquals("data.txt", data.getPathLastComponent());
    }
}