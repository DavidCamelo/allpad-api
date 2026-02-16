package io.allpad.pad.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record HistoryDTO(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) UUID id,
        UUID fileId,
        String content,
        @JsonFormat(shape = JsonFormat.Shape.NUMBER, timezone = "UTC") Instant createdAt) {
}
