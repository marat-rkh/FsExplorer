package fs.explorer.utils;

import java.util.HashSet;
import java.util.Set;

public class FileTypeInfo {
    private static final Set<String> TEXT_FILE_EXTENSIONS = new HashSet<>();
    static {
        TEXT_FILE_EXTENSIONS.add("txt");
        TEXT_FILE_EXTENSIONS.add("log");
        TEXT_FILE_EXTENSIONS.add("xml");
        TEXT_FILE_EXTENSIONS.add("html");
        TEXT_FILE_EXTENSIONS.add("json");
        TEXT_FILE_EXTENSIONS.add("java");
        TEXT_FILE_EXTENSIONS.add("py");
        TEXT_FILE_EXTENSIONS.add("cpp");
        TEXT_FILE_EXTENSIONS.add("c");
        TEXT_FILE_EXTENSIONS.add("hpp");
        TEXT_FILE_EXTENSIONS.add("h");
        TEXT_FILE_EXTENSIONS.add("go");
        TEXT_FILE_EXTENSIONS.add("js");
        TEXT_FILE_EXTENSIONS.add("hs");
    }

    private static final Set<String> IMG_FILE_EXTENSIONS = new HashSet<>();
    static {
        IMG_FILE_EXTENSIONS.add("jpeg");
        IMG_FILE_EXTENSIONS.add("jpg");
        IMG_FILE_EXTENSIONS.add("png");
        IMG_FILE_EXTENSIONS.add("bmp");
        IMG_FILE_EXTENSIONS.add("gif");
        IMG_FILE_EXTENSIONS.add("ico");
        IMG_FILE_EXTENSIONS.add("svg");
        IMG_FILE_EXTENSIONS.add("tif");
        IMG_FILE_EXTENSIONS.add("tiff");
    }

    public static boolean isTextFile(String path) {
        return TEXT_FILE_EXTENSIONS.contains(getExtension(path).toLowerCase());
    }

    public static boolean isImageFile(String path) {
        return IMG_FILE_EXTENSIONS.contains(getExtension(path).toLowerCase());
    }

    public static String getExtension(String path) {
        if(path == null) {
            return "";
        }
        int lastDotIndex = path.lastIndexOf(".");
        if(lastDotIndex != -1 && lastDotIndex != 0) {
            return path.substring(lastDotIndex + 1);
        }
        return "";
    }
}
