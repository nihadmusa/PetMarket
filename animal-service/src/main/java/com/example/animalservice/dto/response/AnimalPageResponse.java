package com.example.animalservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalPageResponse {
    private List<AnimalResponseDto> content;
    private int totalElements;
    private int totalPages;
    private int size;
}
