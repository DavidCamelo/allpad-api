package io.allpad.auth.error;

import com.auth0.jwt.exceptions.TokenExpiredException;
import io.allpad.auth.api.AuthController;
import io.allpad.auth.api.RoleController;
import io.allpad.auth.api.UserController;
import io.allpad.auth.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice(assignableTypes = { AuthController.class, RoleController.class, UserController.class })
public class AuthControllerAdvice {

    @ExceptionHandler(value = { BadCredentialsException.class })
    public ResponseEntity<ErrorDTO> handleBadCredentialsException(BadCredentialsException ex) {
        return buildError(ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = { AuthorizationDeniedException.class })
    public ResponseEntity<ErrorDTO> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        return buildError(ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = { AuthException.class })
    public ResponseEntity<ErrorDTO> handleAuthException(AuthException ex) {
        return buildError(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = { UserNotFoundException.class })
    public ResponseEntity<ErrorDTO> handleUserNotFoundException(UserNotFoundException ex) {
        return buildError(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { RoleNotFoundException.class })
    public ResponseEntity<ErrorDTO> handleRoleNotFoundException(RoleNotFoundException ex) {
        return buildError(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { TokenExpiredException.class })
    public ResponseEntity<ErrorDTO> handleTokenExpiredException(TokenExpiredException ex) {
        return buildError(ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = { UserExistsException.class })
    public ResponseEntity<ErrorDTO> handleUserExistsException(UserExistsException ex) {
        return buildError(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = { RoleExistsException.class })
    public ResponseEntity<ErrorDTO> handleRoleExistsException(RoleExistsException ex) {
        return buildError(ex, HttpStatus.CONFLICT);
    }

    private ResponseEntity<ErrorDTO> buildError(Exception ex, HttpStatus status) {
        var errorDTO = ErrorDTO.builder().message(ex.getMessage()).timestamp(Instant.now()).build();
        log.error("Error message: {}, timestamp: {}", errorDTO.message(), errorDTO.timestamp(), ex);
        return new ResponseEntity<>(errorDTO, status);
    }
}
