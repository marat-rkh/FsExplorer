package fs.explorer.utils;

public class FTPPathUtils {
    public static String append(String parentPath, String childPath) {
        String parentNormal = parentPath.trim().replaceAll("/$", "");
        String childNormal = childPath.trim().replaceAll("^/", "");
        if(childNormal.isEmpty()) {
            return parentNormal;
        }
        return parentNormal + "/" + childNormal;
    }
}
