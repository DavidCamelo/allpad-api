package io.allpad.pad.dto;

import java.util.UUID;

public record TinyFileDTO(
        UUID id,
        UUID padId,
        String name,
        Short pane,
        Boolean isOpen) {
}
