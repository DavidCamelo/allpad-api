package io.allpad.pad.mapper.impl;

import io.allpad.pad.dto.FileDTO;
import io.allpad.pad.entity.File;
import io.allpad.pad.mapper.FileMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FileMapperImpl implements FileMapper {

    @Override
    public FileDTO map(File file) {
        if (file == null) {
            return null;
        }
        return FileDTO.builder()
                .id(file.getId())
                .padId(file.getPad().getId())
                .name(file.getName())
                .content(file.getContent())
                .language(file.getLanguage())
                .pane(file.getPane())
                .isOpen(file.getIsOpen())
                .build();
    }

    @Override
    public void map(FileDTO fileDTO, File file) {
        if (fileDTO == null || file == null) {
            return;
        }
        if (fileDTO.isOpen()) {
            file.setContent(fileDTO.content());
        }
        file.setName(fileDTO.name());
        file.setLanguage(fileDTO.language());
        file.setPane(fileDTO.pane());
        file.setIsOpen(fileDTO.isOpen());
    }

    @Override
    public List<FileDTO> map(List<File> fileList) {
        return fileList.stream().map(this::map).toList();
    }
}
