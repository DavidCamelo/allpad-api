package io.allpad.pad.error;

import io.allpad.pad.api.FileController;
import io.allpad.pad.api.HistoryController;
import io.allpad.pad.api.PadController;
import io.allpad.pad.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice(assignableTypes = { PadController.class, FileController.class, HistoryController.class })
public class PadControllerAdvice {

    @ExceptionHandler(value = { UserNotFoundException.class })
    public ResponseEntity<ErrorDTO> handleUserNotFoundException(UserNotFoundException ex) {
        return buildError(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { AuthException.class })
    public ResponseEntity<ErrorDTO> handleAuthException(AuthException ex) {
        return buildError(ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = { PadNotFoundException.class })
    public ResponseEntity<ErrorDTO> handlePadNotFoundException(PadNotFoundException ex) {
        return buildError(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { FileNotFoundException.class })
    public ResponseEntity<ErrorDTO> handleFileNotFoundException(FileNotFoundException ex) {
        return buildError(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { HistoryNotFoundException.class })
    public ResponseEntity<ErrorDTO> handleHistoryNotFoundException(HistoryNotFoundException ex) {
        return buildError(ex, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ErrorDTO> buildError(Exception ex, HttpStatus status) {
        var errorDTO = ErrorDTO.builder().message(ex.getMessage()).timestamp(Instant.now()).build();
        log.error("Error message: {}, timestamp: {}", errorDTO.message(), errorDTO.timestamp(), ex);
        return new ResponseEntity<>(errorDTO, status);
    }
}
