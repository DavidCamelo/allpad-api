package io.allpad.pad.error;

public class HistoryNotFoundException extends RuntimeException {

    public HistoryNotFoundException(String message) {
        super(message);
    }
}
