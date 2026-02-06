package io.allpad.pad.service;

import io.allpad.pad.dto.HistoryDTO;

import java.util.List;

public interface HistoryService {
    HistoryDTO create(HistoryDTO historyDTO);

    HistoryDTO getById(Long id);

    List<HistoryDTO> getHistoriesByFileId(Long id);

    List<HistoryDTO> getAll();

    HistoryDTO update(Long id, HistoryDTO historyDTO);

    void delete(Long id);
}
