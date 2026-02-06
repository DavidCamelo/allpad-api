package io.allpad.auth.config;

import io.allpad.auth.entity.Role;
import io.allpad.auth.entity.User;
import io.allpad.auth.repository.RoleRepository;
import io.allpad.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class InitUsersAndRoles {

    @Bean
    public CommandLineRunner commandLineRunner(RoleRepository roleRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return _ -> {
            var admin = "admin";
            var mod = "mod";
            var user = "user";
            var adminRole = createRole(roleRepository, admin);
            var modRole = createRole(roleRepository, mod);
            var userRole = createRole(roleRepository, user);
            createUser(userRepository, passwordEncoder, admin, Set.of(adminRole, modRole, userRole));
            createUser(userRepository, passwordEncoder, mod, Set.of(modRole, userRole));
            createUser(userRepository, passwordEncoder, user, Set.of(userRole));
        };
    }

    private Role createRole(RoleRepository roleRepository, String roleName) {
        return roleRepository.findByName("ROLE_" + roleName.toUpperCase())
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_" + roleName.toUpperCase()).build()));
    }

    private void createUser(UserRepository userRepository, PasswordEncoder passwordEncoder,
            String username, Set<Role> roles) {
        if (userRepository.findByUsername(username).isEmpty()) {
            var user = User.builder()
                    .name(username)
                    .lastName(username)
                    .email(username + "@allpad.io")
                    .username(username)
                    .password(passwordEncoder.encode(username + "_password"))
                    .roles(roles)
                    .build();
            userRepository.save(user);
        }
    }
}
