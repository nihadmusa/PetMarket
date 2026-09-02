package com.example.animalservice.dto.request;

import com.example.animalservice.util.AnimalGender;
import com.example.animalservice.util.AnimalStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AnimalRequestDto {
    @NotBlank(message = "ad bos ola bilmez")
    @Size(min = 2, max = 50)
    private String name;

    @NotBlank(message = "tip bos ola bilmez")
    private String type;

    private AnimalStatus status;

    @NotBlank(message = "sort bos ola bilmez")
    private String breed;

    @NotNull(message = "cins bos ola bilmez")
    private AnimalGender gender;

    @NotNull(message = "yas bos ola bilmez")
    @Min(0)
    private Integer age;

    @NotNull(message = "qiymet bos ola bilmez")
    @DecimalMin(value = "0.01")
    private BigDecimal price;

    @NotBlank(message = "seher daxil edin")
    private String city;

    private String description;
}

