package fs.explorer;

public class TestEnvironment {
    private static final String TEST_FTP = "testFtp";

    public static boolean ftpTestsNeeded() {
        return "true".equalsIgnoreCase(System.getProperty(TEST_FTP));
    }
}
