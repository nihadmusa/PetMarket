package com.example.favoriteservice.dto.response;

import java.util.UUID;

public record UserContactResponseDto(
        UUID id,
        String fullName,
        String phoneNumber
) {
}
