package com.example.favoriteservice.exception;

public class LoginRequiredException extends RuntimeException {
    public LoginRequiredException() {
        super("login teleb olunur");
    }
}
