package io.allpad.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthDTO(
        UserDTO user,
        Long accessTokenExpiration,
        Long refreshTokenExpiration,
        @JsonIgnore String accessToken,
        @JsonIgnore String refreshToken) {
}
