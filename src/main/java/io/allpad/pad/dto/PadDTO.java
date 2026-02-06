package io.allpad.pad.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PadDTO(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) Long id,
        String name,
        LastStateDTO lastState) {
}
