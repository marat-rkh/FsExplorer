package fs.explorer.utils;

import fs.explorer.providers.dirtree.FsPath;

public class OSInfo {
    private static String osName = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return osName.contains("win");
    }

    public static boolean isMac() {
        return osName.contains("mac");
    }

    public static boolean isUnix() {
        return osName.contains("nix") || osName.contains("nux") || osName.contains("aix");
    }

    public static FsPath getRootFsPath() {
        if(OSInfo.isWindows()) {
            return new FsPath("C:\\", /*isDirectory*/true, "C:\\");
        } else if(OSInfo.isMac() || OSInfo.isUnix()) {
            return new FsPath("/", /*isDirectory*/true, "/");
        } else {
            throw new IllegalStateException("Unsupported OS");
        }
    }
}
