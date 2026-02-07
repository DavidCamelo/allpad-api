package io.allpad.auth.security;

import io.allpad.auth.dto.UserDTO;
import io.allpad.auth.entity.User;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private final User user;
    private final UserDTO userDTO;

    public CustomUserDetails(String username, @Nullable String password, Collection<? extends GrantedAuthority> authorities, User user, UserDTO userDTO) {
        super(username, password, authorities);
        this.user = user;
        this.userDTO = userDTO;
    }
}
