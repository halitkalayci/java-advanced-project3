package com.smartorder.orderservice.api;

import com.smartorder.orderservice.application.exception.CatalogProductInactiveException;
import com.smartorder.orderservice.application.exception.CatalogProductNotFoundException;
import com.smartorder.orderservice.application.exception.CatalogUnavailableException;
import com.smartorder.orderservice.application.exception.MixedCurrenciesNotAllowedException;
import com.smartorder.orderservice.application.exception.OrderItemQuantityException;
import com.smartorder.orderservice.application.service.OrderNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Maps application exceptions to appropriate HTTP statuses. Caller errors are
 * 4xx; an unreachable catalog is 503 (transient) so clients can retry rather
 * than treating it as their own bad request.
 */
@RestControllerAdvice
public class ProblemDetailsExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ProblemResponse> handleOrderNotFound(OrderNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "order_not_found", ex.getMessage());
    }

    @ExceptionHandler(CatalogProductNotFoundException.class)
    public ResponseEntity<ProblemResponse> handleCatalogProductNotFound(CatalogProductNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "catalog_product_not_found", ex.getMessage());
    }

    @ExceptionHandler({
            CatalogProductInactiveException.class,
            MixedCurrenciesNotAllowedException.class,
            OrderItemQuantityException.class
    })
    public ResponseEntity<ProblemResponse> handleUnprocessable(RuntimeException ex) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "order_not_processable", ex.getMessage());
    }

    @ExceptionHandler(CatalogUnavailableException.class)
    public ResponseEntity<ProblemResponse> handleCatalogUnavailable(CatalogUnavailableException ex) {
        return problem(HttpStatus.SERVICE_UNAVAILABLE, "catalog_unavailable", ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<ProblemResponse> handleValidation(Exception ex) {
        return problem(HttpStatus.BAD_REQUEST, "validation_error", ex.getMessage());
    }

    private ResponseEntity<ProblemResponse> problem(HttpStatus status, String type, String detail) {
        return ResponseEntity.status(status).body(new ProblemResponse(status.value(), type, detail));
    }

    public record ProblemResponse(int status, String type, String detail) {}
}
