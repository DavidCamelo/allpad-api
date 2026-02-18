package io.allpad.stripe.error;

import io.allpad.stripe.api.PlanController;
import io.allpad.stripe.api.StripeSubscriptionController;
import io.allpad.stripe.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice(assignableTypes = { StripeSubscriptionController.class, PlanController.class})
public class StripeSubscriptionControllerAdvice {

    @ExceptionHandler(value = { PlanNotFoundException.class })
    public ResponseEntity<ErrorDTO> handlePlanNotFoundException(PlanNotFoundException ex) {
        return buildError(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { StripeSubscriptionException.class })
    public ResponseEntity<ErrorDTO> handleStripeSubscriptionException(StripeSubscriptionException ex) {
        return buildError(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorDTO> buildError(Exception ex, HttpStatus status) {
        var errorDTO = ErrorDTO.builder().message(ex.getMessage()).timestamp(Instant.now()).build();
        log.error("Error message: {}, timestamp: {}", errorDTO.message(), errorDTO.timestamp(), ex);
        return new ResponseEntity<>(errorDTO, status);
    }
}
