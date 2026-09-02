package com.example.animalservice.exception;

public class InvalidImageCountException extends RuntimeException {
    public InvalidImageCountException(String message) {
        super(message);
    }
}