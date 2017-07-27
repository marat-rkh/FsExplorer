package fs.explorer.datasource;

public class FsPath {
    private final String path;
    private final String name;

    public FsPath(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public String getPath() { return path; }

    public String getName() { return name; }
}
