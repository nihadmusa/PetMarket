package com.example.animalservice.exception;

public class LoginRequiredException extends RuntimeException {
    public LoginRequiredException() {
        super("login teleb olunur");
    }
}
