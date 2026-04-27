package io.allpad.auth.dto;

import lombok.Builder;

@Builder
public record TinyUserDTO(
        String email,
        String username) {
}
