package io.allpad.piston.error;

import io.allpad.piston.api.PistonController;
import io.allpad.piston.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice(assignableTypes = { PistonController.class })
public class PistonControllerAdvice {

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<ErrorDTO> handleException(Exception ex) {
        return buildError(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorDTO> buildError(Exception ex, HttpStatus status) {
        var errorDTO = ErrorDTO.builder().message(ex.getMessage()).timestamp(Instant.now()).build();
        log.error("Error message: {}, timestamp: {}", errorDTO.message(), errorDTO.timestamp(), ex);
        return new ResponseEntity<>(errorDTO, status);
    }
}
