package io.allpad.pad.service;

import io.allpad.pad.dto.FileDTO;
import io.allpad.pad.entity.File;

import java.util.List;

public interface FileService {
    FileDTO create(FileDTO fileDTO);

    FileDTO getById(Long id);

    File findById(Long id);

    List<FileDTO> getFilesByPadId(Long id);

    List<FileDTO> getAll();

    FileDTO update(Long id, FileDTO fileDTO);

    void delete(Long id);
}
