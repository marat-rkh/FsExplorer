package fs.explorer.providers.dirtree;

import fs.explorer.providers.dirtree.path.ArchiveEntryPath;
import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.path.PathContainer;

public class TreeNodeData {
    private final String label;
    private final PathContainer path;

    public TreeNodeData(String label, FsPath fsPath) {
        this.label = label;
        this.path = new PathContainer(fsPath);
    }

    public TreeNodeData(String label, ArchiveEntryPath archiveEntryPath) {
        this.label = label;
        this.path = new PathContainer(archiveEntryPath);
    }

    public String getLabel() { return label; }

    public PathContainer getPath() { return path; }

    @Override
    public String toString() {
        return label;
    }
}
