package io.allpad.auth.service;

import io.allpad.auth.dto.UserDTO;
import io.allpad.auth.entity.User;

import java.util.List;

public interface UserService {
    UserDTO create(UserDTO userDTO);

    void updatePassword(String username, String password);

    UserDTO getByUsername(String username);

    User getUserByUsername(String username);

    List<UserDTO> getAll();

    UserDTO assignRoleToUser(String username, String roleName);

    UserDTO removeRoleFromUser(String username, String roleName);

    UserDTO map(User user);
}
