package com.example.favoriteservice.controller;

import com.example.favoriteservice.dto.response.FavoriteResponseDto;
import com.example.favoriteservice.exception.LoginRequiredException;
import com.example.favoriteservice.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/favorite")
@RequiredArgsConstructor
public class FavoritesController {

    private final FavoriteService service;

    @GetMapping("/check/{animalId}")
    public ResponseEntity<Boolean> check(
            @PathVariable UUID animalId,
            @RequestHeader(value = "User-Id", required = false) UUID userId) {
        if (userId == null) {
            throw new LoginRequiredException();
        }
        return ResponseEntity.ok(service.check(userId, animalId));
    }

    @PostMapping("/{animalId}")
    public ResponseEntity<Void> favorite(
            @PathVariable UUID animalId,
            @RequestHeader(value = "User-Id", required = false) UUID userId) {
        if (userId == null) {
            throw new LoginRequiredException();
        }
        service.favorite(userId, animalId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<FavoriteResponseDto>> getFavorites(
            @RequestHeader(value = "User-Id", required = false) UUID userId) {
        if (userId == null) {
            throw new LoginRequiredException();
        }
        return ResponseEntity.ok(service.getFavorites(userId));
    }

    @DeleteMapping("/{animalId}")
    public ResponseEntity<Void> unfavorite(
            @PathVariable UUID animalId,
            @RequestHeader(value = "User-Id", required = false) UUID userId) {
        if (userId == null) {
            throw new LoginRequiredException();
        }
        service.unfavorite(userId, animalId);
        return ResponseEntity.ok().build();
    }

}
