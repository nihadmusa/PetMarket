package com.example.favoriteservice.dto.response;

import com.example.favoriteservice.util.AnimalGender;
import com.example.favoriteservice.util.AnimalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AnimalResponseDto {

    private UUID id;
    private UUID userId;
    private UserContactResponseDto seller;
    private String name;
    private String type;
    private String breed;
    private AnimalGender gender;
    private Integer age;
    private BigDecimal price;
    private String city;
    private String description;
    private AnimalStatus status;
    private Long viewCount;
    private List<String> images;
}
