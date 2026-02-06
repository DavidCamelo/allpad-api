package io.allpad.pad.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record HistoryDTO(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) Long id,
        Long fileId,
        String content) {
}
