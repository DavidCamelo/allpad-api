package io.allpad.stripe.dto;

import lombok.Builder;

@Builder
public record PlanDTO(
        String id,
        String name,
        String description,
        String priceId,
        Long amount,
        Long unitAmount,
        String currency,
        String interval,
        PlanLimitsDTO planLimits) {
}
