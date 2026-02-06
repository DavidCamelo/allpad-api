package io.allpad.pad.mapper.impl;

import io.allpad.pad.dto.LastStateDTO;
import io.allpad.pad.dto.PadDTO;
import io.allpad.pad.entity.Pad;
import io.allpad.pad.mapper.PadMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PadMapperImpl implements PadMapper {

    @Override
    public PadDTO map(Pad pad) {
        if (pad == null) {
            return null;
        }
        return PadDTO.builder()
                .id(pad.getId())
                .name(pad.getName())
                .lastState(LastStateDTO.builder()
                        .activeFiles(pad.getLastState().getActiveFiles())
                        .activePane(pad.getLastState().getActivePane())
                        .layout(pad.getLastState().getLayout())
                        .build())
                .build();
    }

    @Override
    public void map(PadDTO padDTO, Pad pad) {
        if (padDTO == null || pad == null) {
            return;
        }
        pad.setName(padDTO.name());
        pad.getLastState().setActiveFiles(padDTO.lastState().activeFiles());
        pad.getLastState().setActivePane(padDTO.lastState().activePane());
        pad.getLastState().setLayout(padDTO.lastState().layout());
    }

    @Override
    public List<PadDTO> map(List<Pad> padList) {
        return padList.stream().map(this::map).toList();
    }
}
