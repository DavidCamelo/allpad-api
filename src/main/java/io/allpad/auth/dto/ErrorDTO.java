package io.allpad.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ErrorDTO(
        String message,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss z", timezone = "UTC") Instant timestamp) {
}
