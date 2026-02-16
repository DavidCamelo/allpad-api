package io.allpad.pad.service.impl;

import io.allpad.pad.dto.HistoryDTO;
import io.allpad.pad.entity.History;
import io.allpad.pad.error.AuthException;
import io.allpad.pad.error.HistoryNotFoundException;
import io.allpad.pad.mapper.HistoryMapper;
import io.allpad.pad.repository.HistoryRepository;
import io.allpad.pad.service.FileService;
import io.allpad.pad.service.HistoryService;
import io.allpad.pad.service.PadService;
import io.allpad.pad.utils.ContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {
    private final HistoryMapper historyMapper;
    private final HistoryRepository historyRepository;
    private final FileService fileService;
    private final PadService padService;
    private final ContextUtils contextUtils;

    @Override
    public HistoryDTO create(HistoryDTO historyDTO) {
        var history = new History();
        history.setFile(fileService.findById(historyDTO.fileId()));
        history.setUser(contextUtils.getUser());
        history.setCreatedAt(Instant.now());
        return upsert(historyDTO, history);
    }

    @Override
    public HistoryDTO getById(UUID id) {
        return historyMapper.map(findById(id));
    }

    private History findById(UUID id) {
        var history = historyRepository.findById(id)
                .orElseThrow(() -> new HistoryNotFoundException(String.format("History with id %s not found", id)));
        if (history.getUser().equals(contextUtils.getUser())) {
            return history;
        }
        throw new AuthException("User not authorized to access this history");
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Override
    public List<HistoryDTO> getAll() {
        return historyMapper.map(historyRepository.findAll());
    }

    @Override
    public List<HistoryDTO> getHistoriesByPadIdAndFileId(UUID padId, UUID fileId) {
        var pad = padService.findById(padId);
        var file = fileService.findById(fileId);
        if (file.getPad().equals(pad)) {
            return historyMapper.map(historyRepository.findAllByFile(file));
        }
        throw new AuthException("User not authorized to access this history");
    }

    @Override
    public HistoryDTO update(UUID id, HistoryDTO historyDTO) {
        return upsert(historyDTO, findById(id));
    }

    @Override
    public void delete(UUID id) {
        historyRepository.delete(findById(id));
    }

    private HistoryDTO upsert(HistoryDTO historyDTO, History history) {
        historyMapper.map(historyDTO, history);
        return historyMapper.map(historyRepository.save(history));
    }
}
