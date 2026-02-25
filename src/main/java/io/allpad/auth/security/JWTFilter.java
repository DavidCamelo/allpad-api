package io.allpad.auth.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import io.allpad.auth.dto.ErrorDTO;
import io.allpad.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final JsonMapper jsonMapper = new JsonMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getServletPath().contains("auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        var cookies = request.getCookies();
        var jwtToken = "";
        if (cookies != null) {
            for (var cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }
        var username = "";
        if (jwtToken != null && !jwtToken.isEmpty()) {
            try {
                username = jwtService.validateTokenAndGetUsername(jwtToken);
            } catch (JWTVerificationException e) {
                log.error("Invalid JWT token: {}", e.getMessage());
                var errorDTO = ErrorDTO.builder().message(String.format("Invalid JWT token: %s", e.getMessage()))
                        .timestamp(Instant.now()).build();
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(jsonMapper.writeValueAsString(errorDTO));
                return;
            }
        }

        if (!"".equals(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userDetailsService.loadUserByUsername(username);
            var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }
}
