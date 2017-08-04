package fs.explorer.providers.dirtree.path;

public class PathContainer {
    private final Object holder;
    private final Type type;

    private enum Type {
        FS_PATH,
        ARCHIVE_ENTRY_PATH
    }

    public PathContainer(FsPath fsPath) {
        holder = fsPath;
        type = Type.FS_PATH;
    }

    public PathContainer(ArchiveEntryPath archiveEntryPath) {
        holder = archiveEntryPath;
        type = Type.ARCHIVE_ENTRY_PATH;
    }

    public boolean isFsPath() {
        return type == Type.FS_PATH;
    }

    public boolean isArchiveEntryPath() {
        return type == Type.ARCHIVE_ENTRY_PATH;
    }

    public FsPath asFsPath() {
        return (FsPath) holder;
    }

    public ArchiveEntryPath asArchiveEntryPath() {
        return (ArchiveEntryPath) holder;
    }
}
