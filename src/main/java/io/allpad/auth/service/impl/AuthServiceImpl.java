package io.allpad.auth.service.impl;

import io.allpad.auth.config.JWTProperties;
import io.allpad.auth.dto.AuthDTO;
import io.allpad.auth.dto.TokenDTO;
import io.allpad.auth.dto.UserDTO;
import io.allpad.auth.error.AuthException;
import io.allpad.auth.security.CustomUserDetails;
import io.allpad.auth.service.AuthService;
import io.allpad.auth.service.JwtService;
import io.allpad.auth.service.RefreshTokenService;
import io.allpad.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtService jwtService;
    private final JWTProperties jwtProperties;

    @Override
    public AuthDTO signUp(UserDTO userDTO) {
        return login(userService.create(userDTO));
    }

    @Override
    public AuthDTO login(UserDTO userDTO) {
        var authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userDTO.username(), userDTO.password()));
        if (authentication.isAuthenticated()) {
            var user = (CustomUserDetails) authentication.getPrincipal();
            assert user != null;
            return AuthDTO.builder()
                    .user(user.getUserDTO())
                    .accessTokenExpiration(jwtProperties.accessTokenExpiration())
                    .refreshTokenExpiration(jwtProperties.refreshTokenExpiration())
                    .accessToken(jwtService.generateAccessToken(userDTO.username(), userDTO.roles()))
                    .refreshToken(refreshTokenService.createRefreshToken(userDTO.username()).getToken())
                    .build();
        }
        throw new AuthException("Authentication failed");
    }

    @Override
    public void logout(TokenDTO tokenDTO) {
        refreshTokenService.deleteRefreshToken(tokenDTO.token());
    }

    @Override
    public AuthDTO refreshToken(TokenDTO tokenDTO) {
        var userDTO = refreshTokenService.verifyExpiration(tokenDTO.token());
        return AuthDTO.builder()
                .user(userDTO)
                .accessTokenExpiration(jwtProperties.accessTokenExpiration())
                .refreshTokenExpiration(jwtProperties.refreshTokenExpiration())
                .accessToken(jwtService.generateAccessToken(userDTO.username(), userDTO.roles()))
                .refreshToken(tokenDTO.token())
                .build();
    }

    @Override
    public void recoverPassword(UserDTO userDTO) {
        var user = userService.getUserByUsername(userDTO.username());
        var token = jwtService.generateResetPasswordToken(user.getUsername());
        // TODO send email with token
    }

    @Override
    public void resetPassword(TokenDTO tokenDTO) {
        var username = jwtService.validateTokenAndGetUsername(tokenDTO.token());
        userService.updatePassword(username, tokenDTO.password());
    }
}
