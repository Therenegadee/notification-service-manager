package com.github.therenegade.notification.manager.exceptions.rest;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<String> errors = new ArrayList<>();
        e.getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));
        String errorMessage = "Invalid request params were found: " + String.join(", ", errors);
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(errorMessage));
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<String> errors = new ArrayList<>();
        e.getConstraintViolations().forEach(violation -> errors.add(violation.getMessage()));
        String errorMessage = "Invalid request params were found: " + String.join(", ", errors);
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(errorMessage));
    }

    public record ErrorResponse(String errorMessage) {
    }
}
