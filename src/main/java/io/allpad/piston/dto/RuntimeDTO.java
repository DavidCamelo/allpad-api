package io.allpad.piston.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RuntimeDTO (
        String language,
        String version,
        List<String> aliases,
        String runtime) {
}
