package com.example.notificationservice.exception;

public class LoginRequiredException extends RuntimeException {
    public LoginRequiredException() {
        super("login teleb olunur");
    }
}