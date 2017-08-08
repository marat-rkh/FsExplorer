package fs.explorer;

public class TestEnvironment {
    private static final String TEST_FTP = "testFtp";
    private static final String TEST_ASYNC = "testAsync";

    public static boolean ftpTestsNeeded() {
        return "true".equalsIgnoreCase(System.getProperty(TEST_FTP));
    }

    public static boolean asyncTestsNeeded() {
        return "true".equalsIgnoreCase(System.getProperty(TEST_ASYNC));
    }
}
