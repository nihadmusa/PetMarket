package com.example.animalservice.dto.request;

import com.example.animalservice.util.AnimalGender;
import com.example.animalservice.util.AnimalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimalUpdateDto {
    private String name;
    private String type;
    private AnimalStatus status;
    private String breed;
    private AnimalGender gender;
    private Integer age;
    private BigDecimal price;
    private String city;
    private String description;
}