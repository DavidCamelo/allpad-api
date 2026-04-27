package io.allpad.utils;

import io.allpad.auth.dto.UserDTO;
import io.allpad.auth.entity.User;
import io.allpad.auth.error.UserNotFoundException;
import io.allpad.auth.repository.UserRepository;
import io.allpad.auth.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContextUtils {
    private final UserRepository userRepository;

    public CustomUserDetails getCustomUserDetails() {
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails;
        }
        throw new UserNotFoundException("User not found");
    }

    public User getUser() {
        return userRepository.findByEmail(getUserDTO().email()).orElseThrow(
                () -> new UserNotFoundException(String.format("User with email %s not found", getUserDTO().email())));
    }

    public UserDTO getUserDTO() {
        return getCustomUserDetails().getUserDTO();
    }
}
