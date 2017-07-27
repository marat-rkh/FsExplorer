package fs.explorer.datasource;

public class TreeNodeData {
    private final FsPath fsPath;

    public TreeNodeData(FsPath fsPath) {
        this.fsPath = fsPath;
    }

    public FsPath getFsPath() { return fsPath; }

    @Override
    public String toString() {
        return fsPath.getName();
    }
}
