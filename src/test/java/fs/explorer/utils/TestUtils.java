package fs.explorer.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class TestUtils {
    static void copyDirectory(Path src, Path dst) throws IOException {
        Path srcParent = src.getParent();
        List<Path> paths = Files.walk(src).collect(Collectors.toList());
        for (Path p : paths) {
            Path relative = srcParent.relativize(p);
            Path copyPath = Paths.get(dst.toString(), relative.toString());
            if (Files.isDirectory(p)) {
                Files.createDirectory(copyPath);
            } else {
                Files.copy(p, copyPath);
            }
        }
    }
}
