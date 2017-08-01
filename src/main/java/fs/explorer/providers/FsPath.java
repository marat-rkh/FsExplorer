package fs.explorer.providers;

public final class FsPath {
    // TODO consider replacing this with java.nio.file.Path
    private final String path;
    private final boolean isDirectory;
    private final String lastComponent;

    public FsPath(String path, boolean isDirectory, String lastComponent) {
        this.path = path;
        this.isDirectory = isDirectory;
        this.lastComponent = lastComponent;
    }

    public String getPath() { return path; }

    public boolean isDirectory() { return isDirectory; }

    public String getLastComponent() { return lastComponent; }

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
                isDirectory == other.isDirectory &&
                (lastComponent == null ?
                        other.lastComponent == null :
                        lastComponent.equals(other.lastComponent));
    }

    @Override
    public int hashCode() {
        int res = 17;
        res = 31 * res + (path == null ? 0 : path.hashCode());
        res = 31 * res + Boolean.hashCode(isDirectory);
        res = 31 * res + (lastComponent == null ? 0 : lastComponent.hashCode());
        return res;
    }

    @Override
    public String toString() { return path; }
}
