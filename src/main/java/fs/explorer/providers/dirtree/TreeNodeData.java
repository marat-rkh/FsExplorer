package fs.explorer.providers.dirtree;

import fs.explorer.providers.dirtree.path.ArchiveEntryPath;
import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.path.PathContainer;
import fs.explorer.providers.dirtree.path.TargetType;

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

    public boolean pathTargetIsDirectory() {
        if(path.isFsPath()) {
            return path.asFsPath().isDirectory();
        } else if(path.isArchiveEntryPath()) {
            return path.asArchiveEntryPath().isDirectory();
        } else {
            throw new IllegalStateException("unexpected PathContainer state");
        }
    }

    public String getPathLastComponent() {
        if(path.isFsPath()) {
            return path.asFsPath().getLastComponent();
        } else if(path.isArchiveEntryPath()) {
            return path.asArchiveEntryPath().getLastComponent();
        } else {
            throw new IllegalStateException("unexpected PathContainer state");
        }
    }

    public TargetType getPathTargetType() {
        if(path.isFsPath()) {
            return path.asFsPath().getTargetType();
        } else if(path.isArchiveEntryPath()) {
            return path.asArchiveEntryPath().getTargetType();
        } else {
            throw new IllegalStateException("unexpected PathContainer state");
        }
    }
}
