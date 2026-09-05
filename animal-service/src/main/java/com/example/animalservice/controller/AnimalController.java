package com.example.animalservice.controller;

import com.example.animalservice.dto.request.AnimalRequestDto;
import com.example.animalservice.dto.request.AnimalUpdateDto;
import com.example.animalservice.dto.response.AnimalPageResponse;
import com.example.animalservice.dto.response.AnimalResponseDto;
import com.example.animalservice.exception.LoginRequiredException;
import com.example.animalservice.service.AnimalService;
import com.example.animalservice.util.AnimalGender;
import com.example.animalservice.util.AnimalStatus;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/animal")
@RequiredArgsConstructor
public class AnimalController {
    private final AnimalService service;

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponseDto> getAnimal(@PathVariable UUID id) {
        return ResponseEntity.ok().body(service.getAnimal(id));
    }

    @GetMapping
    public ResponseEntity<AnimalPageResponse> getAnimals(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) AnimalGender gender,
            @RequestParam(required = false) AnimalStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        var animals = service.getAnimals(
                type, breed, city, gender, status, minPrice, maxPrice,
                page, size, sortBy, sortDir);
        return animals.getContent().isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok().body(animals);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> addAnimal(
            @RequestHeader(value = "User-Id", required = false) UUID userId,
            @RequestPart("dto") @Valid AnimalRequestDto dto,
            @RequestPart("images") MultipartFile[] images) {
        if (userId == null) {
            throw new LoginRequiredException();
        }
        UUID animalId = service.addAnimal(userId, dto, images);
        return ResponseEntity.ok(animalId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AnimalResponseDto> updateAnimal(
            @PathVariable UUID id,
            @RequestHeader(value = "User-Id", required = false) UUID userId,
            @RequestBody @Valid AnimalUpdateDto dto) {
        if (userId == null) {
            throw new LoginRequiredException();
        }
        return ResponseEntity.ok().body(service.updateAnimal(id, userId, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @RequestHeader(value = "User-Id", required = false) UUID userId) {
        if (userId == null) {
            throw new LoginRequiredException();
        }
        service.deleteAnimal(id, userId);
        return ResponseEntity.ok().build();
    }

}
