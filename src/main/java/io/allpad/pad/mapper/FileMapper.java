package io.allpad.pad.mapper;

import io.allpad.pad.dto.FileDTO;
import io.allpad.pad.entity.File;
import io.allpad.pad.entity.Pad;

import java.util.List;

public interface FileMapper {
    FileDTO map(File file);

    void map(FileDTO fileDTO, File file, Pad pad);

    List<FileDTO> map(List<File> fileList);
}
