package fs.explorer.providers.dirtree.remote;

import fs.explorer.TestEnvironment;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static fs.explorer.providers.dirtree.remote.TestUtils.rebexTestServer;
import static fs.explorer.providers.dirtree.remote.TestUtils.tele2TestServer;
import static fs.explorer.providers.dirtree.remote.TestUtils.tele2TestServerNoCredentials;

public class FTPConnectionTest {
    @Before
    public void setUp() {
        Assume.assumeTrue(TestEnvironment.ftpTestsNeeded());
    }

    @Test
    public void opensAndClosesConnection() throws FTPException {
        try (FTPConnection connection = new FTPConnection(rebexTestServer())) {
            connection.open();
        }
    }

    @Test
    public void opensAndClosesConnectionWithAnonymousUser() throws FTPException {
        try (FTPConnection connection = new FTPConnection(tele2TestServer())) {
            connection.open();
        }
    }

    @Test
    public void opensAndClosesConnectionWithAnonymousUserImplicitly() throws FTPException {
        try (FTPConnection connection = new FTPConnection(tele2TestServerNoCredentials())) {
            connection.open();
        }
    }

    @Test(expected = IOException.class)
    public void failsToOpenAlreadyOpenConnection() throws FTPException, IOException {
        try (FTPConnection connection = new FTPConnection(tele2TestServerNoCredentials())) {
            connection.open();
            try {
                connection.open();
            } catch (FTPException e) {
                // rethrow a new type of exception just to separate
                // it from the first open and close exception
                throw new IOException();
            }
        }
    }

    @Test(expected = IOException.class)
    public void failsToReopenClosedConnection() throws FTPException, IOException {
        FTPConnection connection = new FTPConnection(tele2TestServer());
        try {
            connection.open();
        } finally {
            connection.close();
        }
        try {
            connection.open();
        } catch (FTPException e) {
            throw new IOException();
        } finally {
            connection.close();
        }
    }

    @Test
    public void closesConnectionEvenIfNotOpened() throws FTPException {
        FTPConnection connection = new FTPConnection(tele2TestServer());
        connection.close();
    }

    @Test
    public void closesConnectionEvenIfAlreadyClosed() throws FTPException {
        FTPConnection connection = new FTPConnection(tele2TestServer());
        connection.close();
        connection.close();
    }
}