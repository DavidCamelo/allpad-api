package io.allpad.stripe.error;

public class PlanNotFoundException extends StripeSubscriptionException {

    public PlanNotFoundException(String message) {
        super(message);
    }
}
