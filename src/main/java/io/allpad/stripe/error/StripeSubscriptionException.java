package io.allpad.stripe.error;

public class StripeSubscriptionException extends RuntimeException {

    public StripeSubscriptionException(String message) {
        super(message);
    }
}
