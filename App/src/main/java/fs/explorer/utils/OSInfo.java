package fs.explorer.utils;

import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.path.TargetType;

public class OSInfo {
    private static String osName = System.getProperty("os.name").toLowerCase();

    public static FsPath getRootFsPath() {
        if (isWindows()) {
            return new FsPath("C:\\", TargetType.DIRECTORY, "C:\\");
        } else if (isMac() || isUnix()) {
            return new FsPath("/", TargetType.DIRECTORY, "/");
        } else {
            throw new IllegalStateException("Unsupported OS");
        }
    }

    private static boolean isWindows() {
        return osName.contains("win");
    }

    private static boolean isMac() {
        return osName.contains("mac");
    }

    private static boolean isUnix() {
        return osName.contains("nix") || osName.contains("nux") || osName.contains("aix");
    }
}
