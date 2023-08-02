package com.example.services;

public class ConcursException extends RuntimeException{
    public ConcursException() {
    }

    public ConcursException(String message) {
        super(message);
    }

    public ConcursException(String message, Throwable cause) {
        super(message, cause);
    }
}
