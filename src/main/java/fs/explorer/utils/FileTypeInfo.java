package fs.explorer.utils;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        return TEXT_FILE_EXTENSIONS.contains(getExtension(path));
    }

    public static boolean isImageFile(String path) {
        return IMG_FILE_EXTENSIONS.contains(getExtension(path));
    }

    // TODO there are better alternatives, see:
    // https://commons.apache.org/proper/commons-io/javadocs/api-2.5/org/apache/commons/io/FilenameUtils.html#getExtension(java.lang.String)
    public static String getExtension(String path) {
        if(path == null) {
            return "";
        }
        String fileName = getLastComponent(path);
        if(fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if(lastDotIndex != -1 && lastDotIndex != 0) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    private static String getLastComponent(String path) {
        try {
            Path fileName = Paths.get(path).getFileName();
            return fileName == null ? "" : fileName.toString();
        } catch (InvalidPathException e) {
            return "";
        }
    }
}
