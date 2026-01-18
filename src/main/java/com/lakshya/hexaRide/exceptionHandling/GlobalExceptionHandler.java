package com.lakshya.hexaRide.exceptionHandling;

import com.lakshya.hexaRide.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handle Resource Not Found (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    // 2. Handle Resource Already Exists / Conflict (409)
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceAlreadyExists(ResourceAlreadyExistsException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI());
    }

    // 3. Handle Invalid Credentials / Bad Requests (401 or 400)
    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCredentials(InvalidCredentialException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    // 4. Handle any other HexaRideException (General Business Logic Error)
    @ExceptionHandler(HexaRideException.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneralBusinessException(HexaRideException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }

    // 5. Global Catch-all for unexpected System Errors (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse("An unexpected error occurred: " + ex.getMessage(), 
                                  HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }

    // define the helper class
    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(String message, HttpStatus status, String path) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }
}
