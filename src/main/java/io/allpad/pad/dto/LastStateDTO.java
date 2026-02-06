package io.allpad.pad.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Map;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LastStateDTO(
        Map<Short, Long> activeFiles,
        Short activePane,
        String layout) {
}
