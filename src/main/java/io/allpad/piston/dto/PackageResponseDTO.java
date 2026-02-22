package io.allpad.piston.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PackageResponseDTO(
        String language,
        String language_version,
        Boolean installed) {
}
