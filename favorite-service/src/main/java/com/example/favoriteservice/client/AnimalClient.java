package com.example.favoriteservice.client;

import com.example.favoriteservice.dto.response.AnimalResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(url = "http://localhost:8082", name = "animal-service")
public interface AnimalClient {
    @GetMapping("/api/v1/animal/{id}")
    AnimalResponseDto getAnimal(@PathVariable UUID id);
}
