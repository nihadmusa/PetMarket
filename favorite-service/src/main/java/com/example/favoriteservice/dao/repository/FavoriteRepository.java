package com.example.favoriteservice.dao.repository;

import com.example.favoriteservice.dao.entity.FavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteEntity, UUID> {

    Optional<FavoriteEntity> findByUserIdAndAnimalId(UUID userId, UUID animalId);

    List<FavoriteEntity> findAllByUserId(UUID userId);

    boolean existsByUserIdAndAnimalId(UUID userId, UUID animalId);

    void deleteByUserIdAndAnimalId(UUID userId, UUID animalId);
}
