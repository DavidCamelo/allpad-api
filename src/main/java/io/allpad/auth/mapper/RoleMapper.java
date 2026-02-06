package io.allpad.auth.mapper;

import io.allpad.auth.dto.RoleDTO;
import io.allpad.auth.entity.Role;

import java.util.List;

public interface RoleMapper {
    RoleDTO map(Role role);

    void map(RoleDTO roleDTO, Role role);

    List<RoleDTO> map(List<Role> roleList);
}
