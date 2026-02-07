package io.allpad.pad.service.impl;

import io.allpad.pad.dto.HistoryDTO;
import io.allpad.pad.entity.History;
import io.allpad.pad.error.HistoryNotFoundException;
import io.allpad.pad.mapper.HistoryMapper;
import io.allpad.pad.repository.HistoryRepository;
import io.allpad.pad.service.FileService;
import io.allpad.pad.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {
    private final HistoryMapper historyMapper;
    private final HistoryRepository historyRepository;
    private final FileService fileService;

    @Override
    public HistoryDTO create(HistoryDTO historyDTO) {
        return upsert(historyDTO, new History());
    }

    @Override
    public HistoryDTO getById(UUID id) {
        return historyMapper.map(findById(id));
    }

    private History findById(UUID id) {
        return historyRepository.findById(id)
                .orElseThrow(() -> new HistoryNotFoundException(String.format("History with id %s not found", id)));
    }

    @Override
    public List<HistoryDTO> getHistoriesByFileId(UUID id) {
        return historyMapper.map(historyRepository.findAllByFile(fileService.findById(id)));
    }

    @Override
    public List<HistoryDTO> getAll() {
        return historyMapper.map(historyRepository.findAll());
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
        historyMapper.map(historyDTO, history, fileService.findById(historyDTO.fileId()));
        return historyMapper.map(historyRepository.save(history));
    }
}
