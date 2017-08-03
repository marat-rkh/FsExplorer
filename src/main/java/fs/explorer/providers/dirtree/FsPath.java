package fs.explorer.providers.dirtree;

public final class FsPath {
    private final String path;
    private final TargetType targetType;
    private final String lastComponent;

    public enum TargetType {
        DIRECTORY,
        ZIP_ARCHIEVE,
        FILE
    }

    public FsPath(String path, TargetType targetType, String lastComponent) {
        this.path = path;
        this.targetType = targetType;
        this.lastComponent = lastComponent;
    }

    public String getPath() { return path; }

    public TargetType getTargetType() { return targetType; }

    public String getLastComponent() { return lastComponent; }

    public boolean isDirectory() {
        return targetType == TargetType.DIRECTORY;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(!(obj instanceof FsPath)) {
            return false;
        }
        FsPath other = (FsPath) obj;
        return (path == null ? other.path == null : path.equals(other.path)) &&
                targetType == other.targetType &&
                (lastComponent == null ?
                        other.lastComponent == null :
                        lastComponent.equals(other.lastComponent));
    }

    @Override
    public int hashCode() {
        int res = 17;
        res = 31 * res + (path == null ? 0 : path.hashCode());
        res = 31 * res + targetType.hashCode();
        res = 31 * res + (lastComponent == null ? 0 : lastComponent.hashCode());
        return res;
    }

    @Override
    public String toString() { return path; }
}
