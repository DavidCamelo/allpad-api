package io.allpad.pad.mapper;

import io.allpad.pad.dto.PadDTO;
import io.allpad.pad.entity.Pad;
import java.util.List;

public interface PadMapper {
    PadDTO map(Pad pad);

    void map(PadDTO padDTO, Pad pad);

    List<PadDTO> map(List<Pad> padList);
}
