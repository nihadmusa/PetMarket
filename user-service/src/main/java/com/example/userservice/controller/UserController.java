package com.example.userservice.controller;

import com.example.userservice.dto.request.UserUpdateDto;
import com.example.userservice.dto.response.UserContactResponseDto;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

    @PatchMapping
    public ResponseEntity<UserContactResponseDto> updateUser(
            @RequestHeader(value = "User-Id", required = false) UUID userId,
            @RequestBody UserUpdateDto dto) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().body(service.updateUser(userId, dto));
    }
}
