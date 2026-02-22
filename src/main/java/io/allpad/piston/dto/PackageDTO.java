package io.allpad.piston.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PackageDTO(
        String language,
        String version,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) Boolean installed) {
}
