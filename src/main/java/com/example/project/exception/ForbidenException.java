package com.example.project.exception;

public class ForbidenException extends RuntimeException {
    public ForbidenException(final String message) {
        super(message);
    }
}
