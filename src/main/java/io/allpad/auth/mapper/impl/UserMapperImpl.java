package io.allpad.auth.mapper.impl;

import io.allpad.auth.dto.UserDTO;
import io.allpad.auth.entity.Role;
import io.allpad.auth.entity.User;
import io.allpad.auth.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO map(User user, String password) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .name(user.getName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(password)
                .encryptionKey(user.getEncryptionKey())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .build();
    }

    @Override
    public void map(UserDTO userDTO, User user) {
        if (userDTO == null || user == null) {
            return;
        }
        user.setName(userDTO.name());
        user.setLastName(userDTO.lastName());
        user.setUsername(userDTO.username());
        user.setEmail(userDTO.email());
    }

    @Override
    public List<UserDTO> map(List<User> userList) {
        return userList.stream().map(user -> map(user, null)).toList();
    }
}
