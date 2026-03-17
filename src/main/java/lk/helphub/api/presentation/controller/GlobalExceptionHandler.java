package lk.helphub.api.presentation.controller;

import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lk.helphub.api.domain.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder()
                .status(false)
                .statusCode(ResponseStatusCode.BAD_REQUEST)
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.builder()
                .status(false)
                .statusCode(ResponseStatusCode.NOT_FOUND)
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        Map<String, List<String>> errors = new HashMap<>();
        errors.put("exception", List.of(ex.getClass().getSimpleName()));
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder()
                .status(false)
                .statusCode(ResponseStatusCode.INTERNAL_SERVER_ERROR)
                .message("An unexpected error occurred")
                .errors(errors)
                .data(ex.getMessage()) // Providing detail in data field for debugging
                .build());
    }
}
