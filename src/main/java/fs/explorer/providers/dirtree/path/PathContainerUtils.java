package fs.explorer.providers.dirtree.path;

// TODO move these methods to TreeNodeData
public class PathContainerUtils {
    public static boolean isDirectoryPath(PathContainer path) {
        if(path.isFsPath()) {
            return path.asFsPath().isDirectory();
        } else if(path.isArchiveEntryPath()) {
            return path.asArchiveEntryPath().isDirectory();
        } else {
            throw new IllegalStateException("unexpected PathContainer state");
        }
    }

    public static String getPathLastComponent(PathContainer path) {
        if(path.isFsPath()) {
            return path.asFsPath().getLastComponent();
        } else if(path.isArchiveEntryPath()) {
            return path.asArchiveEntryPath().getLastComponent();
        } else {
            throw new IllegalStateException("unexpected PathContainer state");
        }
    }

    public static TargetType getPathTargetType(PathContainer path) {
        if(path.isFsPath()) {
            return path.asFsPath().getTargetType();
        } else if(path.isArchiveEntryPath()) {
            return path.asArchiveEntryPath().getTargetType();
        } else {
            throw new IllegalStateException("unexpected PathContainer state");
        }
    }
}
