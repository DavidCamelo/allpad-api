package io.allpad.payment.service;

import io.allpad.payment.dto.SubscriptionDTO;

public interface SubscriptionService {
    SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO);

    void cancelSubscription();

    void handleWebhook(String payload, String sigHeader);
}
