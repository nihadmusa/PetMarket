package com.example.favoriteservice.exception;

public class FavoriNotFoundException extends RuntimeException {
    public FavoriNotFoundException(String message) {
        super(message);
    }
}
