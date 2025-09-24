package com.carsil.userapi.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ApiError {

    private int status;
    private String error;              // Código HTTP (ej: "Not Found")
    private String message;            // Mensaje amigable para el usuario
    private String developerMessage;   // Explicación técnica / detalle para el dev
    private String path;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ApiError(HttpStatus status, String message, String developerMessage, String path) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.developerMessage = developerMessage;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getDeveloperMessage() { return developerMessage; }
    public String getPath() { return path; }
    public LocalDateTime getTimestamp() { return timestamp; }
}