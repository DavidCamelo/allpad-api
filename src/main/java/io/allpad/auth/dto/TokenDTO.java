package io.allpad.auth.dto;

import lombok.Builder;

@Builder
public record TokenDTO(
        String token,
        String password) {
}
