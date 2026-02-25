package io.allpad.auth.service.impl;

import com.auth0.jwt.exceptions.JWTVerificationException;
import io.allpad.auth.dto.UserDTO;
import io.allpad.auth.entity.RefreshToken;
import io.allpad.auth.error.AuthException;
import io.allpad.auth.repository.RefreshTokenRepository;
import io.allpad.auth.service.JwtService;
import io.allpad.auth.service.RefreshTokenService;
import io.allpad.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final JwtService jwtService;

    @Override
    public RefreshToken createRefreshToken(String username, Long expiration) {
        var refreshToken = new RefreshToken();
        refreshToken.setUser(userService.getUserByUsername(username));
        refreshToken.setExpiration(expiration);
        refreshToken.setToken(jwtService.generateRefreshToken(username));
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public UserDTO verifyExpiration(String token) {
        try {
            var username = jwtService.validateTokenAndGetUsername(token);
            var user = userService.getUserByUsername(username);
            var refreshToken = refreshTokenRepository.findByTokenAndUser(token, user).orElseThrow(() -> {
                deleteRefreshToken(token);
                return new AuthException("Invalid refresh token");
            });
            user.setRefreshTokenExpiration(refreshToken.getExpiration());
            return userService.map(refreshToken.getUser());
        } catch (JWTVerificationException ex) {
            deleteRefreshToken(token);
            throw new AuthException(String.format("Invalid JWT token: %s", ex.getMessage()));
        }
    }

    @Override
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
    }
}
