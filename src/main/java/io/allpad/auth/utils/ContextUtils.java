package io.allpad.auth.utils;

import io.allpad.auth.entity.User;
import io.allpad.auth.security.CustomUserDetails;
import io.allpad.pad.error.UserNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ContextUtils {

    public CustomUserDetails getCustomUserDetails() {
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails;
        }
        throw new UserNotFoundException("User not found");
    }

    public User getUser() {
        return getCustomUserDetails().getUser();
    }
}
