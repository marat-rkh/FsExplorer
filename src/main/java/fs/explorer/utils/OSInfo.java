package fs.explorer.utils;

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
}
