package io.allpad.stripe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PistonCredentialsDTO(
        String user,
        String password) {
}
