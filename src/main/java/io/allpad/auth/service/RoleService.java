package io.allpad.auth.service;

import io.allpad.auth.dto.RoleDTO;
import io.allpad.auth.entity.Role;

import java.util.List;

public interface RoleService {
    RoleDTO create(RoleDTO roleDTO);

    Role findByName(String name);

    List<RoleDTO> getAll();

    void delete(Long id);
}
