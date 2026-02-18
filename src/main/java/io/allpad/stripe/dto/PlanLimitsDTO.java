package io.allpad.stripe.dto;

import lombok.Builder;

@Builder
public record PlanLimitsDTO(
        Integer pads,
        Integer filesPerPad,
        Integer historiesPerFile) {
}
