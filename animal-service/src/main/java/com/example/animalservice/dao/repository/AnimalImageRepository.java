package com.example.animalservice.dao.repository;

import com.example.animalservice.dao.entity.AnimalImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnimalImageRepository extends JpaRepository<AnimalImageEntity, UUID> {

    List<AnimalImageEntity> findAllByAnimalIdOrderByPosition(UUID animalId);

    List<AnimalImageEntity> findAllByAnimalIdInOrderByPosition(Collection<UUID> animalIds);

    Optional<AnimalImageEntity> findByIdAndAnimalId(UUID id, UUID animalId);

    long countByAnimalId(UUID animalId);
}