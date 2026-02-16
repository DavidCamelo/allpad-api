package io.allpad.pad.service.impl;

import io.allpad.pad.dto.PadDTO;
import io.allpad.pad.entity.Pad;
import io.allpad.pad.error.AuthException;
import io.allpad.pad.error.PadNotFoundException;
import io.allpad.pad.mapper.PadMapper;
import io.allpad.pad.repository.PadRepository;
import io.allpad.pad.service.PadService;
import io.allpad.pad.utils.ContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PadServiceImpl implements PadService {
    private final PadMapper padMapper;
    private final PadRepository padRepository;
    private final ContextUtils contextUtils;

    @Override
    public PadDTO create(PadDTO padDTO) {
        var pad = new Pad();
        pad.setUser(contextUtils.getUser());
        return upsert(padDTO, pad);
    }

    @Override
    public PadDTO getById(UUID id) {
        return padMapper.map(findById(id));
    }

    @Override
    public Pad findById(UUID id) {
        var pad = padRepository.findById(id)
                .orElseThrow(() -> new PadNotFoundException(String.format("Pad with id %s not found", id)));
        if (pad.getUser().equals(contextUtils.getUser())) {
            return pad;
        }
        throw new AuthException("User not authorized to access this pad");
    }

    @Override
    public List<PadDTO> getAll() {
        var userDTO = contextUtils.getUserDTO();
        if (userDTO.roles().contains("ROLE_ADMIN")) {
            return padMapper.map(padRepository.findAll());
        }
        var user = contextUtils.getUser();
        return padMapper.map(padRepository.findAllByUser(user));
    }

    @Override
    public PadDTO update(UUID id, PadDTO padDTO) {
        return upsert(padDTO, findById(id));
    }

    @Override
    public void delete(UUID id) {
        padRepository.delete(findById(id));
    }

    private PadDTO upsert(PadDTO padDTO, Pad pad) {
        padMapper.map(padDTO, pad);
        return padMapper.map(padRepository.save(pad));
    }
}
