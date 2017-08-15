package fs.explorer.controllers;

import fs.explorer.providers.dirtree.remote.FTPConnectionInfo;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class FTPInfoValidatorTest {
    private FTPInfoValidator ftpInfoValidator = new FTPInfoValidator();

    @Test
    public void validatesNoInfoErrorOnNullInfo() {
        Optional<String> res = ftpInfoValidator.validate(null);
        assertEquals(Optional.of(FTPInfoValidator.NO_INFO_ERROR), res);
    }

    @Test
    public void validatesHostRequiredErrorOnEmptyHost() {
        Optional<String> res = ftpInfoValidator.validate(
                new FTPConnectionInfo("", "", "".toCharArray()));
        assertEquals(Optional.of(FTPInfoValidator.HOST_REQUIRED_ERROR), res);
    }

    @Test
    public void validatesHostRequiredErrorOnNullHost() {
        Optional<String> res = ftpInfoValidator.validate(
                new FTPConnectionInfo("", "", "".toCharArray()));
        assertEquals(Optional.of(FTPInfoValidator.HOST_REQUIRED_ERROR), res);
    }

    @Test
    public void validatesUserRequiredErrorWhenPasswordIsPresentAndUserIsNot() {
        Optional<String> res = ftpInfoValidator.validate(
                new FTPConnectionInfo("some.host", "", "pass".toCharArray()));
        assertEquals(Optional.of(FTPInfoValidator.USER_REQUIRED_ERROR), res);
    }
}