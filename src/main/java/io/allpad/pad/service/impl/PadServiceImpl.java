package io.allpad.pad.service.impl;

import io.allpad.pad.dto.PadDTO;
import io.allpad.pad.entity.Pad;
import io.allpad.pad.error.PadNotFoundException;
import io.allpad.pad.mapper.PadMapper;
import io.allpad.pad.repository.PadRepository;
import io.allpad.pad.service.PadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PadServiceImpl implements PadService {
    private final PadMapper padMapper;
    private final PadRepository padRepository;

    @Override
    public PadDTO create(PadDTO padDTO) {
        return upsert(padDTO, new Pad());
    }

    @Override
    public PadDTO getById(Long id) {
        return padMapper.map(findById(id));
    }

    @Override
    public Pad findById(Long id) {
        return padRepository.findById(id)
                .orElseThrow(() -> new PadNotFoundException(String.format("Pad with id %s not found", id)));
    }

    @Override
    public List<PadDTO> getAll() {
        return padMapper.map(padRepository.findAll());
    }

    @Override
    public PadDTO update(Long id, PadDTO padDTO) {
        return upsert(padDTO, findById(id));
    }

    @Override
    public void delete(Long id) {
        padRepository.delete(findById(id));
    }

    private PadDTO upsert(PadDTO padDTO, Pad pad) {
        padMapper.map(padDTO, pad);
        return padMapper.map(padRepository.save(pad));
    }
}
