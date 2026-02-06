package io.allpad.pad.error;

public class PadNotFoundException extends RuntimeException {

    public PadNotFoundException(String message) {
        super(message);
    }
}
