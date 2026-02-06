package io.allpad.auth.mapper;

import io.allpad.auth.dto.UserDTO;
import io.allpad.auth.entity.User;

import java.util.List;

public interface UserMapper {
    UserDTO map(User user, String password);

    void map(UserDTO userDTO, User user);

    List<UserDTO> map(List<User> userList);
}
