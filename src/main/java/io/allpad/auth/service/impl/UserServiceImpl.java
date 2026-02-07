package io.allpad.auth.service.impl;

import io.allpad.auth.dto.UserDTO;
import io.allpad.auth.entity.User;
import io.allpad.auth.error.AuthException;
import io.allpad.auth.error.UserExistsException;
import io.allpad.auth.error.UserNotFoundException;
import io.allpad.auth.mapper.UserMapper;
import io.allpad.auth.repository.UserRepository;
import io.allpad.auth.service.RoleService;
import io.allpad.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO create(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.email()).isPresent()) {
            throw new UserExistsException(String.format("User with email %s already exists", userDTO.email()));
        }
        var userRole = roleService.findByName("ROLE_USER");
        var user = User.builder()
                .name(userDTO.name())
                .lastName(userDTO.lastName())
                .email(userDTO.email())
                .username(userDTO.email().split("@")[0])
                .password(passwordEncoder.encode(userDTO.password()))
                .roles(Set.of(userRole))
                .build();
        return userMapper.map(userRepository.save(user), userDTO.password());
    }

    @Override
    public void updatePassword(String username, String password) {
        var user = getUserByUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public UserDTO getByUsername(String username) {
        var user = getUserByUsername(username);
        return userMapper.map(user, user.getPassword());
    }

    @Override
    public User getUserByUsername(String username) {
            return findUserByUsername(username).orElseThrow(
                () -> new UserNotFoundException(String.format("User with username %s not found", username)));
    }

    @Override
    public List<UserDTO> getAll() {
        return userMapper.map(userRepository.findAll());
    }

    @Override
    public UserDTO assignRoleToUser(String username, String roleName) {
        var user = getUserByUsername(username);
        var role = roleService.findByName(roleName);
        user.getRoles().add(role);
        return userMapper.map(userRepository.save(user), null);
    }

    @Override
    public UserDTO removeRoleFromUser(String username, String roleName) {
        var user = getUserByUsername(username);
        var role = roleService.findByName(roleName);
        user.getRoles().remove(role);
        return userMapper.map(userRepository.save(user), null);
    }

    @Override
    public UserDTO map(User user) {
        return userMapper.map(user, null);
    }

    private Optional<User> findUserByUsername(String username) {
        if (username.contains("@")) {
            return userRepository.findByEmail(username);
        }
        return userRepository.findByUsername(username);
    }
}
