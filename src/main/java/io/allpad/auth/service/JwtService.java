package io.allpad.auth.service;

import java.util.List;

public interface JwtService {
    String generateAccessToken(String username, List<String> roles);

    String generateRefreshToken(String username);

    String generateResetPasswordToken(String username);

    String validateTokenAndGetUsername(String token);
}
