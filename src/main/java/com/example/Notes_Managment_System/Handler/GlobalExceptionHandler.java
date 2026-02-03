package com.example.Notes_Managment_System.Handler;

import com.example.Notes_Managment_System.Dto.ApiResponse;
import com.example.Notes_Managment_System.Dto.ErrorDetails;
import com.example.Notes_Managment_System.Exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ================= COMMON RESPONSE BUILDER ================= */

    private ResponseEntity<ApiResponse<Object>> buildResponse(
            HttpStatus status,
            String message,
            ErrorDetails error
    ) {
        ApiResponse<Object> response = new ApiResponse<>(
                status.value(),
                false,
                message,
                error,
                null
        );
        return new ResponseEntity<>(response, status);
    }

    /* ================= 404 / 403 ================= */

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        ErrorDetails error = new ErrorDetails(
                ex.getField(),
                ex.getCode(),
                ex.getDescrption()
        );

        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                error
        );
    }

    /* ================= 400 VALIDATION ================= */

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            ValidationException ex) {

        ErrorDetails error = new ErrorDetails(
                ex.getField(),
                ex.getCode(),
                ex.getDescription()
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                error
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {

        String description = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorDetails error = new ErrorDetails(
                "multiple_fields",
                "INPUT_VALIDATION_FAILED",
                description
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                error
        );
    }

    /* ================= 401 ================= */

    @ExceptionHandler(BadCreditionalException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(
            BadCreditionalException ex) {

        ErrorDetails error = new ErrorDetails(
                null,
                "BAD_CREDENTIALS",
                ex.getMessage()
        );

        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Authentication failed",
                error
        );
    }

    /* ================= 409 ================= */

    @ExceptionHandler(Duplicated.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateException(
            Duplicated ex) {

        ErrorDetails error = new ErrorDetails(
                ex.getField(),
                ex.getCode(),
                ex.getDescription()
        );

        return buildResponse(
                HttpStatus.CONFLICT,
                "Duplicate entry found",
                error
        );
    }

    /* ================= 500 ================= */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(
            Exception ex) {

        ErrorDetails error = new ErrorDetails(
                null,
                "INTERNAL_SERVER_ERROR",
                ex.getMessage()
        );

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                error
        );
    }
}
