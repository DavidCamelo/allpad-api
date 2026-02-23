package io.allpad.auth.error;

public class RoleAssignedException extends RuntimeException {

    public RoleAssignedException(String message, Throwable cause) {
        super(message, cause);
    }
}
