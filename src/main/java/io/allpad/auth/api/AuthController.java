package io.allpad.auth.api;

import io.allpad.auth.dto.AuthDTO;
import io.allpad.auth.dto.TokenDTO;
import io.allpad.auth.dto.UserDTO;
import io.allpad.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(userDTO));
    }

    @Operation(summary = "Login", description = "User login authentication")
    @PostMapping("login")
    public ResponseEntity<AuthDTO> login(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(authService.login(userDTO));
    }

    @Operation(summary = "Logout", description = "User logout")
    @PostMapping("logout")
    public ResponseEntity<Void> logout(@RequestBody TokenDTO tokenDTO) {
        authService.logout(tokenDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Refresh", description = "Refresh token")
    @PostMapping("refresh")
    public ResponseEntity<AuthDTO> refresh(@RequestBody TokenDTO tokenDTO) {
        return ResponseEntity.ok(authService.refreshToken(tokenDTO));
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
}
