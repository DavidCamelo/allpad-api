package io.allpad.pad.service;

import io.allpad.pad.dto.FileDTO;
import io.allpad.pad.entity.File;

import java.util.List;
import java.util.UUID;

public interface FileService {
    FileDTO create(FileDTO fileDTO);

    FileDTO getById(UUID id);

    File findById(UUID id);

    List<FileDTO> getFilesByPadId(UUID padId);

    FileDTO update(UUID id, FileDTO fileDTO);

    void delete(UUID id);
}
