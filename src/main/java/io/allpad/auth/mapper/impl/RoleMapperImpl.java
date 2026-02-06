package io.allpad.auth.mapper.impl;

import io.allpad.auth.dto.RoleDTO;
import io.allpad.auth.entity.Role;
import io.allpad.auth.mapper.RoleMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public RoleDTO map(Role role) {
        if (role == null) {
            return null;
        }
        return RoleDTO.builder()
                .name(role.getName())
                .build();
    }

    @Override
    public void map(RoleDTO roleDTO, Role role) {
        if (roleDTO == null || role == null) {
            return;
        }
        role.setName(roleDTO.name());
    }

    @Override
    public List<RoleDTO> map(List<Role> roleList) {
        return roleList.stream().map(this::map).toList();
    }
}
