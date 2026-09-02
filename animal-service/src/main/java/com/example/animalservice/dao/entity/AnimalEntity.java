package com.example.animalservice.dao.entity;

import com.example.animalservice.util.AnimalGender;
import com.example.animalservice.util.AnimalStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "animals")
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AnimalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    UUID userId;

    private String name;

    private String type;

    private String breed;

    private AnimalGender gender;

    private Integer age;

    private BigDecimal price;

    private String city;

    private String description;

    @Enumerated(EnumType.STRING)
    private AnimalStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

