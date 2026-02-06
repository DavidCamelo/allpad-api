package io.allpad.auth.error;

public class RoleExistsException extends RuntimeException {

    public RoleExistsException(String message) {
        super(message);
    }
}
