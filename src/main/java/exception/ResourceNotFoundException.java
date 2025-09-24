package com.carsil.userapi.exception;

// Excepción personalizada para cuando no se encuentra un recurso (ej. usuario)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
