package io.allpad.auth.dto;

import lombok.Builder;

@Builder
public record UserTinyDTO(
        String email,
        String username) {
}
