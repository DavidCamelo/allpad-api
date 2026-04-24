package io.allpad.auth.service.impl;

import com.auth0.jwt.exceptions.JWTVerificationException;
import io.allpad.auth.entity.RefreshToken;
import io.allpad.auth.error.AuthException;
import io.allpad.auth.repository.RefreshTokenRepository;
import io.allpad.auth.service.JwtService;
import io.allpad.auth.service.RefreshTokenService;
import io.allpad.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final JwtService jwtService;

    @Override
    public RefreshToken createRefreshToken(String username, Long expiration) {
        var refreshToken = new RefreshToken();
        refreshToken.setUsername(username);
        refreshToken.setExpiration(expiration);
        refreshToken.setToken(jwtService.generateRefreshToken(username));
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Long verifyExpiration(String token) {
        try {
            var username = jwtService.validateTokenAndGetUsername(token);
            var refreshToken = refreshTokenRepository.findByTokenAndUsername(token, username).orElseThrow(() -> {
                deleteRefreshToken(token);
                return new AuthException("Invalid refresh token");
            });
            return refreshToken.getExpiration();
        } catch (JWTVerificationException ex) {
            deleteRefreshToken(token);
            throw new AuthException(String.format("Invalid JWT token: %s", ex.getMessage()));
        }
    }

    @Scheduled(fixedDelay =  10_000)
    public void deleteExpiredRefreshTokens() {
        var now = Instant.now();
        var expiredRefreshTokens = refreshTokenRepository.findAllByExpirationBefore(now.toEpochMilli());
        expiredRefreshTokens.forEach(refreshToken -> {
            userService.evictUserFromCache(refreshToken.getUsername());
            refreshTokenRepository.delete(refreshToken);
        });
    }

    @Override
    public void deleteRefreshToken(String token) {
        var refreshTokenO = refreshTokenRepository.findByToken(token);
        if (refreshTokenO.isPresent()) {
            var refreshToken = refreshTokenO.get();
            userService.evictUserFromCache(refreshToken.getUsername());
            refreshTokenRepository.delete(refreshToken);
        }
    }
}
