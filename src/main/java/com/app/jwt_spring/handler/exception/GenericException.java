package com.app.jwt_spring.handler.exception;

public class GenericException extends RuntimeException {
    public GenericException(String message) {
        super(message);
    }
}
