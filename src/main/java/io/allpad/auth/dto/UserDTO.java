package io.allpad.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDTO(
        String name,
        String lastName,
        String email,
        String username,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String password,
        String encryptionKey,
        List<String> roles,
        @JsonIgnore Long refreshTokenExpiration) {
}
