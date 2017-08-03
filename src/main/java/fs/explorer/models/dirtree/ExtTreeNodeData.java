package fs.explorer.models.dirtree;

import fs.explorer.providers.dirtree.FsPath;
import fs.explorer.providers.dirtree.TreeNodeData;

public class ExtTreeNodeData {
    private final TreeNodeData nodeData;
    private Status status;
    private final Type type;

    public ExtTreeNodeData(TreeNodeData nodeData, Status status, Type type) {
        this.nodeData = nodeData;
        this.status = status;
        this.type = type;
    }

    public TreeNodeData getNodeData() { return nodeData; }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }

    public Type getType() { return type; }

    @Override
    public String toString() {
        return nodeData.toString();
    }

    public enum Status {
        NULL,
        LOADING,
        LOADED
    }

    public enum Type {
        NORMAL,
        FAKE
    }

    public static ExtTreeNodeData fakeNodeData(String text) {
        TreeNodeData nodeData = new TreeNodeData(text, new FsPath("", FsPath.TargetType.FILE, ""));
        return new ExtTreeNodeData(nodeData, Status.LOADED, Type.FAKE);
    }

    public static ExtTreeNodeData nullNodeData(TreeNodeData nodeData) {
        return new ExtTreeNodeData(nodeData, Status.NULL, Type.NORMAL);
    }

    public static ExtTreeNodeData loadedNodeData(TreeNodeData nodeData) {
        return new ExtTreeNodeData(nodeData, Status.LOADED, Type.NORMAL);
    }
}
