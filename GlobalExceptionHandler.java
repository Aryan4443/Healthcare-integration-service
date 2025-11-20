package com.healthbridge.integration.api.error;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleNotFound(EntityNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        detail.setDetail(ex.getMessage());
        return detail;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ProblemDetail handleValidation(Exception ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        if (ex instanceof MethodArgumentNotValidException manv) {
            String errors = manv.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> "%s %s".formatted(error.getField(), error.getDefaultMessage()))
                    .collect(Collectors.joining("; "));
            detail.setDetail(errors);
        } else if (ex instanceof ConstraintViolationException cve) {
            String errors = cve.getConstraintViolations()
                    .stream()
                    .map(violation -> "%s %s".formatted(violation.getPropertyPath(), violation.getMessage()))
                    .collect(Collectors.joining("; "));
            detail.setDetail(errors);
        } else {
            detail.setDetail("Validation failed");
        }
        return detail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setDetail(ex.getMessage());
        return detail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        detail.setDetail("Unexpected error occurred");
        return detail;
    }
}

