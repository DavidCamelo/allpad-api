package io.allpad.pad.mapper.impl;

import io.allpad.pad.dto.HistoryDTO;
import io.allpad.pad.entity.File;
import io.allpad.pad.entity.History;
import io.allpad.pad.mapper.HistoryMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HistoryMapperImpl implements HistoryMapper {

    @Override
    public HistoryDTO map(History history) {
        if (history == null) {
            return null;
        }
        return HistoryDTO.builder()
                .id(history.getId())
                .fileId(history.getFile().getId())
                .content(history.getContent())
                .build();
    }

    @Override
    public void map(HistoryDTO historyDTO, History history, File file) {
        if (historyDTO == null || history == null) {
            return;
        }
        history.setFile(file);
        history.setContent(historyDTO.content());
    }

    @Override
    public List<HistoryDTO> map(List<History> historyList) {
        return historyList.stream().map(this::map).toList();
    }
}
