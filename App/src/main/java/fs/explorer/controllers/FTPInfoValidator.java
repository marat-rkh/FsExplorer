package fs.explorer.controllers;

import fs.explorer.providers.dirtree.remote.FTPConnectionInfo;

import java.util.Optional;

public class FTPInfoValidator {
    static final String NO_INFO_ERROR = "Connection info is empty";
    static final String USER_REQUIRED_ERROR = "Username is required";
    static final String HOST_REQUIRED_ERROR = "Host is required";

    Optional<String> validate(FTPConnectionInfo info) {
        if (info == null) {
            return Optional.of(NO_INFO_ERROR);
        }
        if (info.getHost() == null || info.getHost().isEmpty()) {
            return Optional.of(HOST_REQUIRED_ERROR);
        }
        boolean isUserRequired = (info.getUser() == null || info.getUser().isEmpty()) &&
                info.getPassword() != null &&
                info.getPassword().length != 0;
        if (isUserRequired) {
            return Optional.of(USER_REQUIRED_ERROR);
        }
        return Optional.empty();
    }
}
