package io.allpad.pad.service.impl;

import io.allpad.pad.dto.FileDTO;
import io.allpad.pad.entity.File;
import io.allpad.pad.error.AuthException;
import io.allpad.pad.error.FileNotFoundException;
import io.allpad.pad.mapper.FileMapper;
import io.allpad.pad.repository.FileRepository;
import io.allpad.pad.service.FileService;
import io.allpad.pad.service.PadService;
import io.allpad.pad.utils.ContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileMapper fileMapper;
    private final FileRepository fileRepository;
    private final PadService padService;
    private final ContextUtils contextUtils;

    @Override
    public FileDTO create(FileDTO fileDTO) {
        var file = new File();
        file.setPad(padService.findById(fileDTO.padId()));
        file.setUser(contextUtils.getUser());
        return upsert(fileDTO, file);
    }

    @Override
    public FileDTO getById(UUID id) {
        return fileMapper.map(findById(id));
    }

    @Override
    public File findById(UUID id) {
        var file = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(String.format("File with id %s not found", id)));
        if (file.getUser().equals(contextUtils.getUser())) {
            return file;
        }
        throw new AuthException("User not authorized to access this file");
    }

    @Override
    public List<FileDTO> getFilesByPadId(UUID id) {
        return fileMapper.map(fileRepository.findAllByPad(padService.findById(id)));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Override
    public List<FileDTO> getAll() {
        return fileMapper.map(fileRepository.findAll());
    }

    @Override
    public FileDTO update(UUID id, FileDTO fileDTO) {
        return upsert(fileDTO, findById(id));
    }

    @Override
    public void delete(UUID id) {
        fileRepository.delete(findById(id));
    }

    private FileDTO upsert(FileDTO fileDTO, File file) {
        fileMapper.map(fileDTO, file);
        return fileMapper.map(fileRepository.save(file));
    }
}
