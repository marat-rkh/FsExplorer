package fs.explorer.providers.dirtree.remote;

public class TestUtils {
    public static FTPConnectionInfo rebexTestServer() {
        return new FTPConnectionInfo("test.rebex.net", "demo", "password".toCharArray());
    }

    public static FTPConnectionInfo tele2TestServer() {
        return new FTPConnectionInfo("speedtest.tele2.net", "anonymous", "".toCharArray());
    }

    static FTPConnectionInfo tele2TestServerNoCredentials() {
        return new FTPConnectionInfo("speedtest.tele2.net", "", "".toCharArray());
    }
}
