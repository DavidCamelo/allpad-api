package io.allpad.auth.api;

import io.allpad.auth.dto.AuthDTO;
import io.allpad.auth.dto.TokenDTO;
import io.allpad.auth.dto.UserDTO;
import io.allpad.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth API")
@RestController
@RequestMapping(value = "api/{version}/auth", version = "v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Sign up", description = "User Sign Up")
    @PostMapping("signup")
    public ResponseEntity<AuthDTO> signUp(@RequestBody UserDTO userDTO) {
        var authDTO = authService.signUp(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).headers(createCookieHeaders(authDTO)).body(authDTO);
    }

    @Operation(summary = "Login", description = "User login authentication")
    @PostMapping("login")
    public ResponseEntity<AuthDTO> login(@RequestBody UserDTO userDTO) {
        var authDTO = authService.login(userDTO);
        return ResponseEntity.ok().headers(createCookieHeaders(authDTO)).body(authDTO);
    }

    @Operation(summary = "Logout", description = "User logout")
    @PostMapping("logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            authService.logout(new TokenDTO(refreshToken, null));
        }
        return ResponseEntity.noContent().headers(clearCookieHeaders()).build();
    }

    @Operation(summary = "Refresh", description = "Refresh token")
    @PostMapping("refresh")
    public ResponseEntity<AuthDTO> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(clearCookieHeaders()).build();
        }
        var authDTO = authService.refreshToken(new TokenDTO(refreshToken, null));
        return ResponseEntity.ok().headers(createCookieHeaders(authDTO)).body(authDTO);
    }

    @Operation(summary = "Recover password", description = "User recover password")
    @PostMapping("recover-password")
    public ResponseEntity<Void> recoverPassword(@RequestBody UserDTO userDTO) {
        authService.recoverPassword(userDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update password", description = "User update password")
    @PostMapping("update-password")
    public ResponseEntity<Void> updatePassword(@RequestBody TokenDTO tokenDTO) {
        authService.updatePassword(tokenDTO);
        return ResponseEntity.noContent().build();
    }

    private HttpHeaders createCookieHeaders(AuthDTO authDTO) {
        var headers = new HttpHeaders();
        if (authDTO.accessToken() != null) {
            var maxAge = (authDTO.accessTokenExpiration() - System.currentTimeMillis()) / 1000;
            var accessCookie = ResponseCookie
                    .from("accessToken", authDTO.accessToken())
                    .httpOnly(true)
                    .path("/")
                    .maxAge(maxAge > 0 ? maxAge : 3600)
                    .build();
            headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
        }
        if (authDTO.refreshToken() != null) {
            long maxAge = (authDTO.refreshTokenExpiration() != null ? authDTO.refreshTokenExpiration()
                    : (System.currentTimeMillis() + 86400000L * 7)) - System.currentTimeMillis();
            maxAge = maxAge / 1000;
            var refreshCookie = ResponseCookie
                    .from("refreshToken", authDTO.refreshToken())
                    .httpOnly(true)
                    .path("/")
                    .maxAge(maxAge > 0 ? maxAge : 86400 * 7)
                    .build();
            headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        }
        return headers;
    }

    private HttpHeaders clearCookieHeaders() {
        var headers = new HttpHeaders();
        var accessCookie = ResponseCookie
                .from("accessToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
        var refreshCookie = ResponseCookie
                .from("refreshToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
        headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        return headers;
    }
}
