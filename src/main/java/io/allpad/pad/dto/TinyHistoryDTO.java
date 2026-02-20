package io.allpad.pad.dto;

import java.time.Instant;
import java.util.UUID;

public record TinyHistoryDTO(
        UUID id,
        UUID fileId,
        Instant createdAt) {
}
