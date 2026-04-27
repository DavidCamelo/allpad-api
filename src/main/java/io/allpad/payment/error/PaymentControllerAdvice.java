package io.allpad.payment.error;

import io.allpad.payment.api.PaymentController;
import io.allpad.payment.api.PlanController;
import io.allpad.payment.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice(assignableTypes = { PaymentController.class, PlanController.class})
public class PaymentControllerAdvice {

    @ExceptionHandler(value = { SubscriptionException.class })
    public ResponseEntity<ErrorDTO> handleSubscriptionException(SubscriptionException ex) {
        return buildError(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = { Exception.class, RuntimeException.class })
    public ResponseEntity<ErrorDTO> handleAnyException(Exception ex) {
        return buildError(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorDTO> buildError(Exception ex, HttpStatus status) {
        var errorDTO = ErrorDTO.builder().message(ex.getMessage()).timestamp(Instant.now()).build();
        log.error("Error message: {}, timestamp: {}", errorDTO.message(), errorDTO.timestamp(), ex);
        return new ResponseEntity<>(errorDTO, status);
    }
}
