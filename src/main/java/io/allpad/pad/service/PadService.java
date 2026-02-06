package io.allpad.pad.service;

import io.allpad.pad.dto.PadDTO;
import io.allpad.pad.entity.Pad;

import java.util.List;

public interface PadService {
    PadDTO create(PadDTO padDTO);

    PadDTO getById(Long id);

    Pad findById(Long id);

    List<PadDTO> getAll();

    PadDTO update(Long id, PadDTO padDTO);

    void delete(Long id);
}
