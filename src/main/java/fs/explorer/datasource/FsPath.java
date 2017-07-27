package fs.explorer.datasource;

public class FsPath {
    private final String path;
    private final String name;
    private final boolean isDirectory;

    public FsPath(String path, String name, boolean isDirectory) {
        this.path = path;
        this.name = name;
        this.isDirectory = isDirectory;
    }

    public String getPath() { return path; }

    public String getName() { return name; }

    public boolean isDirectory() { return isDirectory; }
}
