package com.carsil.userapi.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class ApiError {

    private final int status;
    private final String error;
    private final String message;
    private final String developerMessage;
    private final String path;
}