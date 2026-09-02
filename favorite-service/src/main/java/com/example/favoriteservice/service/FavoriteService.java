package com.example.favoriteservice.service;

import com.example.favoriteservice.client.AnimalClient;
import com.example.favoriteservice.dao.entity.FavoriteEntity;
import com.example.favoriteservice.dao.repository.FavoriteRepository;
import com.example.favoriteservice.dto.event.NotificationEvent;
import com.example.favoriteservice.dto.response.AnimalResponseDto;
import com.example.favoriteservice.dto.response.FavoriteResponseDto;
import com.example.favoriteservice.exception.FavoriAlreadyExistsException;
import com.example.favoriteservice.exception.FavoriNotFoundException;
import com.example.favoriteservice.rabbit.NotificationProducer;
import com.example.favoriteservice.util.AnimalStatus;
import com.example.favoriteservice.util.FavoriStatus;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository repository;
    private final AnimalClient animalClient;
    private final NotificationProducer notificationProducer;

    @Transactional
    public void favorite(UUID userId, UUID animalId) {
        if (repository.existsByUserIdAndAnimalId(userId, animalId)) {
            throw new FavoriAlreadyExistsException("Bu elan artiq favorilerinizdedir");
        }
        var entity = FavoriteEntity.builder()
                .userId(userId)
                .animalId(animalId)
                .build();
        repository.save(entity);
        publishFavoriteEvent(userId, animalId);
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponseDto> getFavorites(UUID userId) {
        return repository.findAllByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void unfavorite(UUID userId, UUID animalId) {
        if (!repository.existsByUserIdAndAnimalId(userId, animalId)) {
            throw new FavoriNotFoundException("Favori tapilmadi");
        }
        repository.deleteByUserIdAndAnimalId(userId, animalId);
    }

    @Transactional(readOnly = true)
    public boolean check(UUID userId, UUID animalId) {
        return repository.existsByUserIdAndAnimalId(userId, animalId);
    }

    private FavoriteResponseDto toResponse(FavoriteEntity entity) {
        AnimalResponseDto animal = resolveAnimal(entity.getAnimalId());
        FavoriStatus status = (animal == null || animal.getStatus() == AnimalStatus.DELETED)
                ? FavoriStatus.SILINIB
                : FavoriStatus.ACTIVE;

        return FavoriteResponseDto.builder()
                .id(entity.getId())
                .animalId(entity.getAnimalId())
                .animal(status == FavoriStatus.ACTIVE ? animal : null)
                .status(status)
                .favoritedAt(entity.getCreatedAt())
                .build();
    }

    private AnimalResponseDto resolveAnimal(UUID animalId) {
        try {
            return animalClient.getAnimal(animalId);
        } catch (FeignException.NotFound e) {
            return null;
        }
    }

    private void publishFavoriteEvent(UUID userId, UUID animalId) {
        try {
            AnimalResponseDto animal = resolveAnimal(animalId);
            if (animal == null || animal.getUserId() == null) {
                return;
            }
            notificationProducer.send(NotificationEvent.builder()
                    .eventType("FAVORITE_ADDED")
                    .targetUserId(animal.getUserId())
                    .animalId(animalId)
                    .title("Elaniniz beyenildi")
                    .message("'" + animal.getName() + "' elaninizi bir istifadeci beyendi")
                    .build());
        } catch (Exception e) {
            // ignore notification failures
        }
    }
}
