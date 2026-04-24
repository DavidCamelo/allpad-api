package io.allpad.auth.service;

import io.allpad.auth.entity.RefreshToken;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(String username, Long expiration);

    Long verifyExpiration(String token);

    void deleteRefreshToken(String token);
}
