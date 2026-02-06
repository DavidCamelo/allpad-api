package io.allpad.auth.service.impl;

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
    public RefreshToken createRefreshToken(String username) {
        var refreshToken = new RefreshToken();
        refreshToken.setUser(userService.getUserByUsername(username));
        refreshToken.setToken(jwtService.generateRefreshToken(username));
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public UserDTO verifyExpiration(String token) {
        var username = jwtService.validateTokenAndGetUsername(token);
        var user = userService.getUserByUsername(username);
        var refreshToken = refreshTokenRepository.findByTokenAndUser(token, user).orElseThrow(() -> {
            deleteRefreshToken(token);
            return new AuthException("Invalid refresh token");
        });
        return userService.map(refreshToken.getUser());
    }

    @Override
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
    }
}
