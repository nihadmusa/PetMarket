package com.example.animalservice.client;

import com.example.animalservice.dto.response.UserContactResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(url = "http://localhost:8081",
name = "user-service")
public interface UserClient {
    @GetMapping("/api/v1/user/{id}")
    UserContactResponseDto getUserContact(@PathVariable UUID id);
}
