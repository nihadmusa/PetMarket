package com.example.animalservice.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponseDto(
		int status,
		String message,
		Map<String, String> fieldErrors,
		LocalDateTime timestamp
) {
}
