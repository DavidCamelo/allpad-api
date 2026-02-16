package io.allpad.pad.mapper;

import io.allpad.pad.dto.FileDTO;
import io.allpad.pad.entity.File;

import java.util.List;

public interface FileMapper {
    FileDTO map(File file);

    void map(FileDTO fileDTO, File file);

    List<FileDTO> map(List<File> fileList);
}
