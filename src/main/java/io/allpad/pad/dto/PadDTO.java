package io.allpad.pad.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PadDTO(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) UUID id,
        String name,
        Boolean isActive,
        LastStateDTO lastState) {
}
