package com.example.userservice.controller;

import com.example.userservice.dto.request.RenewRequestdto;
import com.example.userservice.dto.request.SignInRequestDto;
import com.example.userservice.dto.request.SignUpRequestDto;
import com.example.userservice.dto.response.SignInResponseDto;
import com.example.userservice.dto.response.UserContactResponseDto;
import com.example.userservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sign")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("/up")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequestDto dto) {
        service.signUp(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/in")
    public ResponseEntity<SignInResponseDto> signIn(@RequestBody @Valid SignInRequestDto dto) {
        return ResponseEntity.ok().body(service.signIn(dto));
    }

    @PostMapping("/renew")
    public ResponseEntity<SignInResponseDto> renew(@RequestBody RenewRequestdto dto) {
        return ResponseEntity.ok().body(service.renew(dto));
    }



}
