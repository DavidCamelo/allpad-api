package io.allpad.piston.dto;

import java.util.List;

public record ExecuteDTO (
        String language,
        String version,
        List<ExecuteFileDTO> files) {
}
