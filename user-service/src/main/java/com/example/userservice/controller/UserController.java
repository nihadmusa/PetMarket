package com.example.userservice.controller;

import com.example.userservice.dto.response.UserContactResponseDto;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    @GetMapping("/{id}")
    public ResponseEntity<UserContactResponseDto> getUserContact(@PathVariable UUID id){
        return ResponseEntity.ok().body(service.getUserContact(id));
    }
}
