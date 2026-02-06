package io.allpad.auth.service.impl;

import io.allpad.auth.dto.RoleDTO;
import io.allpad.auth.entity.Role;
import io.allpad.auth.error.RoleExistsException;
import io.allpad.auth.error.RoleNotFoundException;
import io.allpad.auth.mapper.RoleMapper;
import io.allpad.auth.repository.RoleRepository;
import io.allpad.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;

    @Override
    public RoleDTO create(RoleDTO roleDTO) {
        if (roleRepository.findByName(roleDTO.name()).isPresent()) {
            throw new RoleExistsException(String.format("Role with name %s already exists", roleDTO.name()));
        }
        return upsert(roleDTO, new Role());
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNotFoundException(String.format("Role with name %s not found", name)));
    }

    @Override
    public List<RoleDTO> getAll() {
        return roleMapper.map(roleRepository.findAll());
    }

    @Override
    public void delete(Long id) {
        roleRepository.delete(findById(id));
    }

    private RoleDTO upsert(RoleDTO roleDTO, Role role) {
        roleMapper.map(roleDTO, role);
        return roleMapper.map(roleRepository.save(role));
    }

    private Role findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(String.format("Role with id %s not found", id)));
    }
}
