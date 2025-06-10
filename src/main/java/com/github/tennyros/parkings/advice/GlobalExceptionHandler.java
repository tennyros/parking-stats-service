package com.github.tennyros.parkings.advice;

import com.github.tennyros.parkings.exception.CarAlreadyParkedException;
import com.github.tennyros.parkings.exception.CarNotFoundException;
import com.github.tennyros.parkings.exception.NoAvailableSpotsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CarNotFoundException.class)
    public ProblemDetail handleCarNotFound(CarNotFoundException ex, HttpServletRequest request) {
        log.error("Car not found: {}", ex.getMessage());
        return buildProblemDetail(HttpStatus.NOT_FOUND, ex.getMessage(), "Not found", request);
    }

    @ExceptionHandler(CarAlreadyParkedException.class)
    public ProblemDetail handleCarAlreadyParked(CarAlreadyParkedException ex, HttpServletRequest request) {
        log.error("Car already parked: {}", ex.getMessage());
        return buildProblemDetail(HttpStatus.CONFLICT, ex.getMessage(), "Conflict", request);
    }

    @ExceptionHandler(NoAvailableSpotsException.class)
    public ProblemDetail handleNoAvailableSpots(NoAvailableSpotsException ex, HttpServletRequest request) {
        log.error("No free spot for type: {}", ex.getMessage());
        return buildProblemDetail(HttpStatus.NOT_FOUND, ex.getMessage(), "Not found", request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.error("Invalid argument: {}", ex.getMessage());
        return buildProblemDetail(HttpStatus.BAD_REQUEST, ex.getMessage(), "Bad request", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Validation failed for request {}: {}", request.getRequestURI(), ex.getMessage());

        ProblemDetail problemDetail = buildProblemDetail(HttpStatus.BAD_REQUEST, "One or more fields are invalid",
                "Validation failed", request);

        List<Map<String, Object>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    Map<String, Object> errorDetail = new HashMap<>();
                    errorDetail.put("field", error.getField());
                    errorDetail.put("message", error.getDefaultMessage());
                    errorDetail.put("rejectedValue", error.getRejectedValue());
                    return errorDetail;
                })
                .toList();

        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, String message,
                                             String title, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        problemDetail.setTitle(title);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", OffsetDateTime.now());
        return problemDetail;
    }
}
