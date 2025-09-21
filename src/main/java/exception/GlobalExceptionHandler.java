package com.carsil.userapi.exception;

import com.carsil.userapi.exception.ApiError;
import com.carsil.userapi.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND,
                "Recurso no encontrado",
                ex.getMessage() + " → Esto significa que el recurso solicitado no existe o fue eliminado.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.CONFLICT,
                "Conflicto de datos",
                "El registro que intentas guardar viola una restricción de la base de datos. " +
                        "Posiblemente estás intentando registrar un valor duplicado en un campo único. " +
                        "Detalles técnicos: " + ex.getMostSpecificCause().getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        StringBuilder details = new StringBuilder("Errores de validación: ");
        ex.getBindingResult().getFieldErrors().forEach(error ->
                details.append("Campo '").append(error.getField())
                        .append("' → ").append(error.getDefaultMessage()).append(". ")
        );

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Datos inválidos en la solicitud",
                details.toString(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                "Ocurrió un error inesperado mientras se procesaba la solicitud. " +
                        "Por favor inténtalo más tarde o contacta al administrador. " +
                        "Detalles: " + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}