package io.allpad.pad.mapper;

import io.allpad.pad.dto.HistoryDTO;
import io.allpad.pad.entity.File;
import io.allpad.pad.entity.History;

import java.util.List;

public interface HistoryMapper {
    HistoryDTO map(History history);

    void map(HistoryDTO historyDTO, History history, File file);

    List<HistoryDTO> map(List<History> historyList);
}
