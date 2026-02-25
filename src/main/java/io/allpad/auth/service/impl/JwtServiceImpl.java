package io.allpad.auth.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.allpad.auth.config.JWTProperties;
import io.allpad.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final JWTProperties jwtProperties;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(jwtProperties.secret());
    }

    @Override
    public String generateAccessToken(String username, List<String> roles) {
        return generateToken(username, roles, jwtProperties.accessTokenExpiration());
    }

    @Override
    public String generateRefreshToken(String username) {
        return generateToken(username, Collections.emptyList(), jwtProperties.refreshTokenExpiration());
    }

    @Override
    public String generateResetPasswordToken(String username) {
        return generateToken(username, Collections.emptyList(), jwtProperties.resetPasswordExpiration());
    }

    @Override
    public String validateTokenAndGetUsername(String token) {
        return JWT.require(getAlgorithm())
                .build()
                .verify(token)
                .getSubject();
    }

    private String generateToken(String username, List<String> roles, Long expiration) {
        return JWT.create()
                .withSubject(username)
                .withClaim("roles", roles)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusMillis(expiration))
                .sign(getAlgorithm());
    }
}
