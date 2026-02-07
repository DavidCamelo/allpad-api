package io.allpad.pad.service;

import io.allpad.pad.dto.PadDTO;
import io.allpad.pad.entity.Pad;

import java.util.List;
import java.util.UUID;

public interface PadService {
    PadDTO create(PadDTO padDTO);

    PadDTO getById(UUID id);

    Pad findById(UUID id);

    List<PadDTO> getAll();

    PadDTO update(UUID id, PadDTO padDTO);

    void delete(UUID id);
}
