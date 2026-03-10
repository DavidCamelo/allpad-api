package io.allpad.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SubscriptionDTO(
        String planId,
        String priceId,
        String subscriptionId,
        String clientSecret,
        String status) {
}
