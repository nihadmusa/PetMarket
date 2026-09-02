package com.example.animalservice.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super("Bu heyvan sizin deyil");
    }
}
