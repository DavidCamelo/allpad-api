package io.allpad.stripe.service;

import io.allpad.stripe.dto.StripeSubscriptionDTO;

public interface StripeSubscriptionService {
    StripeSubscriptionDTO createSubscription(StripeSubscriptionDTO stripeSubscriptionDTO);

    void cancelSubscription();

    void handleWebhook(String payload, String sigHeader);
}
