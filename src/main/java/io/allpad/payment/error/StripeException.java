package io.allpad.payment.error;

public class StripeException extends RuntimeException {

    public StripeException(String message) {
        super(message);
    }
}
