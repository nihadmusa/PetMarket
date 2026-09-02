package com.example.favoriteservice.dto.response;

import com.example.favoriteservice.util.FavoriStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record FavoriteResponseDto(
        UUID id,
        UUID animalId,
        AnimalResponseDto animal,
        FavoriStatus status,
        LocalDateTime favoritedAt
) {
}
