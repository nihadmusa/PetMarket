package com.example.favoriteservice.exception;

public class FavoriAlreadyExistsException extends RuntimeException {
    public FavoriAlreadyExistsException(String message) {
        super(message);
    }
}
