package fs.explorer.providers.dirtree.path;

import fs.explorer.utils.FileTypeInfo;

import java.nio.file.Files;
import java.nio.file.Path;

public final class FsPath {
    private final String path;
    private final TargetType targetType;
    private final String lastComponent;

    public FsPath(String path, TargetType targetType, String lastComponent) {
        this.path = path;
        this.targetType = targetType;
        this.lastComponent = lastComponent;
    }

    public String getPath() {
        return path;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public String getLastComponent() {
        return lastComponent;
    }

    public boolean isDirectory() {
        return targetType == TargetType.DIRECTORY;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FsPath)) {
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
    public String toString() {
        return path;
    }

    public static FsPath fromPath(Path path) {
        if (path == null) {
            return null;
        }
        Path fileName = path.getFileName();
        String lastComponent = fileName == null ? "" : fileName.toString();
        String pathStr = path.toString();
        TargetType targetType;
        if (Files.isDirectory(path)) {
            targetType = TargetType.DIRECTORY;
        } else if (FileTypeInfo.isZipArchive(pathStr)) {
            targetType = TargetType.ZIP_ARCHIVE;
        } else {
            targetType = TargetType.FILE;
        }
        return new FsPath(pathStr, targetType, lastComponent);
    }
}
