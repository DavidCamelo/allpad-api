package io.allpad.auth.security;

import io.allpad.auth.dto.ErrorDTO;
import io.allpad.auth.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JsonMapper jsonMapper = new JsonMapper();

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JWTFilter jwtFilter) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v*/auth/**", "/swagger-ui/**", "/v3/v*/api-docs", "/v3/v*/api-docs/**")
                        .permitAll()
                        .requestMatchers("/api/v*/roles/**").hasRole("ADMIN")
                        .requestMatchers("/api/v*/users/**").hasAnyRole("ADMIN", "MOD")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception.accessDeniedHandler((_, response, accessDeniedException) -> {
                    var errorDTO = ErrorDTO.builder().message(accessDeniedException.getMessage())
                            .timestamp(Instant.now()).build();
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(jsonMapper.writeValueAsString(errorDTO));
                }))
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return username -> {
            var user = userService.getUserByUsername(username);
            var userDTO = userService.getByUsername(username);
            var authorities = userDTO.roles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
            return new CustomUserDetails(userDTO.username(), userDTO.password(), authorities, user, userDTO);
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }
}