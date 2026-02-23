package io.allpad.piston.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PackageRequestDTO(
        String language,
        String version) {
}
