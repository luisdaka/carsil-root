package com.carsil.userapi.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private final int status;
    private final String error;
    private final String title;
    private final String message;
    private final String path;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp; // Momento del error

    // Constructor con 4 parámetros
    public ApiError(HttpStatus status, String title, String message, String path) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.title = title;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
    // Getters
    public int getStatus() {
        return status;
    }
    public String getError() {
        return error;
    }
    public String getTitle() {
        return title;
    }
    public String getMessage() {
        return message;
    }
    public String getPath() {
        return path;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}