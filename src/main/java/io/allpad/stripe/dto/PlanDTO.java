package io.allpad.stripe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlanDTO(
        String id,
        String priceId,
        String subscriptionId,
        SubscriptionStatusDTO subscriptionStatus,
        String name,
        String description,
        Long amount,
        Long unitAmount,
        String currency,
        String interval,
        PlanLimitsDTO planLimits,
        PistonCredentialsDTO pistonCredentials) {
}
