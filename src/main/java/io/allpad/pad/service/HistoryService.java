package io.allpad.pad.service;

import io.allpad.pad.dto.HistoryDTO;

import java.util.List;
import java.util.UUID;

public interface HistoryService {
    HistoryDTO create(HistoryDTO historyDTO);

    HistoryDTO getById(UUID id);

    List<HistoryDTO> getHistoriesByPadIdAndFileId(UUID padId, UUID fileId);

    void delete(UUID id);
}
