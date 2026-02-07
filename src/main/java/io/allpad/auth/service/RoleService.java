package io.allpad.auth.service;

import io.allpad.auth.dto.RoleDTO;
import io.allpad.auth.entity.Role;

import java.util.List;
import java.util.UUID;

public interface RoleService {
    RoleDTO create(RoleDTO roleDTO);

    Role findByName(String name);

    List<RoleDTO> getAll();

    void delete(UUID id);
}
