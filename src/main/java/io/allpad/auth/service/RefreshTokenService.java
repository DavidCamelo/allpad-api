package io.allpad.auth.service;

import io.allpad.auth.dto.UserDTO;
import io.allpad.auth.entity.RefreshToken;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(String username, Long expiration);

    UserDTO verifyExpiration(String token);

    void deleteRefreshToken(String token);
}
