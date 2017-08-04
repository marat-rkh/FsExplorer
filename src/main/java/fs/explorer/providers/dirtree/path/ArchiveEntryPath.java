package fs.explorer.providers.dirtree.path;

import java.util.Objects;

public final class ArchiveEntryPath {
    private final String archivePath;
    private final String entryPath;
    private final TargetType targetType;
    private final String lastComponent;

    public ArchiveEntryPath(
            String archivePath,
            String entryPath,
            TargetType targetType,
            String lastComponent
    ) {
        this.archivePath = archivePath;
        this.entryPath = entryPath;
        this.targetType = targetType;
        this.lastComponent = lastComponent;
    }

    public boolean isDirectory() {
        return targetType == TargetType.DIRECTORY;
    }

    public String getArchivePath() { return archivePath; }

    public String getEntryPath() { return entryPath; }

    public TargetType getTargetType() { return targetType; }

    public String getLastComponent() { return lastComponent; }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(!(obj instanceof ArchiveEntryPath)) {
            return false;
        }
        ArchiveEntryPath other = (ArchiveEntryPath) obj;
        return Objects.equals(archivePath, other.archivePath) &&
                Objects.equals(entryPath, other.entryPath) &&
                Objects.equals(targetType, other.targetType) &&
                Objects.equals(lastComponent, other.lastComponent);
    }

    @Override
    public int hashCode() {
        int res = 17;
        res = 13 * res + Objects.hashCode(archivePath);
        res = 13 * res + Objects.hashCode(entryPath);
        res = 13 * res + Objects.hashCode(targetType);
        res = 13 * res + Objects.hashCode(lastComponent);
        return res;
    }
}
