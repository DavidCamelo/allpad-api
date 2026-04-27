package io.allpad.auth.service.impl;

import io.allpad.auth.dto.UserDTO;
import io.allpad.auth.entity.User;
import io.allpad.auth.error.UserExistsException;
import io.allpad.auth.error.UserNotFoundException;
import io.allpad.auth.mapper.UserMapper;
import io.allpad.auth.repository.UserRepository;
import io.allpad.auth.service.RoleService;
import io.allpad.auth.service.UserService;
import io.allpad.pad.utils.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
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
                .encryptionKey(EncryptionUtils.createSecretKey())
                .password(passwordEncoder.encode(userDTO.password()))
                .roles(Set.of(userRole))
                .build();
        return userMapper.map(userRepository.save(user));
    }

    @Override
    @CacheEvict(value = "dto::user", key = "#username")
    public void updatePassword(String username, String password) {
        var user = getUserByUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    @Cacheable(value = "dto::user", key = "#username")
    public UserDTO getByUsername(String username) {
        var user = getUserByUsername(username);
        return userMapper.map(user);
    }

    @Override
    public List<UserDTO> getAll() {
        return userMapper.map(userRepository.findAll());
    }

    @Override
    @CacheEvict(value = "dto::user", key = "#username")
    public UserDTO assignRoleToUser(String username, String roleName) {
        var user = getUserByUsername(username);
        var role = roleService.findByName(roleName);
        user.getRoles().add(role);
        return userMapper.map(userRepository.save(user));
    }

    @Override
    @CacheEvict(value = "dto::user", key = "#username")
    public UserDTO removeRoleFromUser(String username, String roleName) {
        var user = getUserByUsername(username);
        var role = roleService.findByName(roleName);
        user.getRoles().remove(role);
        return userMapper.map(userRepository.save(user));
    }

    @Override
    @CacheEvict(value = "dto::user", key = "#username")
    public void evictUserFromCache(String username) {
        log.info("evicting user from cache {}", username);
    }

    private User getUserByUsername(String username) {
        return username.contains("@")
                ? userRepository.findByEmail(username).orElseThrow(
                        () -> new UserNotFoundException(String.format("User with email %s not found", username)))
                : userRepository.findByUsername(username).orElseThrow(
                        () -> new UserNotFoundException(String.format("User with username %s not found", username)));
    }
}
