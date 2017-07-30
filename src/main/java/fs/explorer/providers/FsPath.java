package fs.explorer.providers;

public class FsPath {
    // TODO consider replacing this with java.nio.file.Path
    private final String path;
    private final boolean isDirectory;

    public FsPath(String path, boolean isDirectory) {
        this.path = path;
        this.isDirectory = isDirectory;
    }

    public String getPath() { return path; }

    public boolean isDirectory() { return isDirectory; }
}
