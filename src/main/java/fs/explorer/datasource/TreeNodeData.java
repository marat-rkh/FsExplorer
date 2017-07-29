package fs.explorer.datasource;

public class TreeNodeData {
    private final String label;
    private final FsPath fsPath;

    public TreeNodeData(String label, FsPath fsPath) {
        this.label = label;
        this.fsPath = fsPath;
    }

    public String getLabel() { return label; }

    public FsPath getFsPath() { return fsPath; }

    @Override
    public String toString() {
        return label;
    }
}
