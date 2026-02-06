package io.allpad.auth.security;

import io.allpad.auth.dto.UserDTO;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private final UserDTO userDTO;

    public CustomUserDetails(String username, @Nullable String password,
            Collection<? extends GrantedAuthority> authorities, UserDTO userDTO) {
        super(username, password, authorities);
        this.userDTO = userDTO;
    }

    public CustomUserDetails(String username, @Nullable String password, boolean enabled, boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities,
            UserDTO userDTO) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userDTO = userDTO;
    }
}
