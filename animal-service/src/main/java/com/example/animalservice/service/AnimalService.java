package com.example.animalservice.service;

import com.example.animalservice.client.UserClient;
import com.example.animalservice.dao.entity.AnimalEntity;
import com.example.animalservice.dao.entity.AnimalImageEntity;
import com.example.animalservice.dao.repository.AnimalImageRepository;
import com.example.animalservice.dao.repository.AnimalRepository;
import com.example.animalservice.dto.request.AnimalRequestDto;
import com.example.animalservice.dto.event.NotificationEvent;
import com.example.animalservice.dto.response.AnimalPageResponse;
import com.example.animalservice.dto.response.AnimalResponseDto;
import com.example.animalservice.dto.response.UserContactResponseDto;
import com.example.animalservice.exception.AnimalNotFoundException;
import com.example.animalservice.exception.ForbiddenException;
import com.example.animalservice.exception.ImageNotFoundException;
import com.example.animalservice.exception.InvalidImageCountException;
import com.example.animalservice.mapper.AnimalMapper;
import com.example.animalservice.rabbit.NotificationProducer;
import com.example.animalservice.specification.AnimalSpecification;
import com.example.animalservice.util.AnimalGender;
import com.example.animalservice.util.AnimalStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnimalService {
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("price", "age", "city", "createdAt");
    private static final int MIN_IMAGES = 2;
    private static final int MAX_IMAGES = 5;

    private final AnimalRepository repository;
    private final AnimalImageRepository imageRepository;
    private final AnimalMapper mapper;
    private final UserClient userClient;
    private final ViewCountService viewCountService;
    private final ImageStorageService imageStorageService;
    private final NotificationProducer notificationProducer;

    @Transactional
    public UUID addAnimal(UUID userId, AnimalRequestDto dto, MultipartFile[] images) {
        var animal = mapper.dtoToEntity(dto);
        animal.setUserId(userId);
        if (animal.getStatus() == null) {
            animal.setStatus(AnimalStatus.ACTIVE);
        }
        animal = repository.save(animal);
        saveImages(animal.getId(), List.of(images));
        notificationProducer.send(NotificationEvent.builder()
                .eventType("ANIMAL_CREATED")
                .targetUserId(userId)
                .animalId(animal.getId())
                .title("Yeni elan yaradildi")
                .message("'" + animal.getName() + "' elaniniz uqurla yaradildi")
                .build());
        return animal.getId();
    }

    public AnimalResponseDto getAnimal(UUID id) {
        var animal = repository.findById(id).orElseThrow(
                () -> new AnimalNotFoundException("Heyvan Tapilmadi")
        );
        var views = viewCountService.incrementView(id);

        var response = mapper.entityToDto(animal);
        response.setSeller(fetchSeller(animal.getUserId()));
        response.setViewCount(views);
        response.setImages(buildImageUrls(id));
        return response;
    }



    public AnimalImageEntity getImageByAnimalId(UUID imageId, UUID animalId) {
        return imageRepository.findByIdAndAnimalId(imageId, animalId).orElseThrow(
                () -> new ImageNotFoundException("Sekil tapilmadi")
        );
    }

    @Transactional
    public void addImagesToAnimal(UUID animalId, UUID userId, List<MultipartFile> images) {
        var animal = repository.findById(animalId).orElseThrow(
                () -> new AnimalNotFoundException("Heyvan Tapilmadi")
        );
        checkOwnership(animal, userId);
        checkNotDeleted(animal);
        if (images == null || images.stream().anyMatch(MultipartFile::isEmpty)) {
            throw new InvalidImageCountException("Sekil fayli bos ola bilmez");
        }
        long existing = imageRepository.countByAnimalId(animalId);
        if (existing + images.size() > MAX_IMAGES) {
            throw new InvalidImageCountException("En cox 5 sekil yuklenile biler");
        }
        saveImages(animalId, images);
    }

    @Transactional
    public void deleteImage(UUID animalId, UUID userId, UUID imageId) {
        var animal = repository.findById(animalId).orElseThrow(
                () -> new AnimalNotFoundException("Heyvan Tapilmadi")
        );
        checkOwnership(animal, userId);
        checkNotDeleted(animal);
        long existing = imageRepository.countByAnimalId(animalId);
        if (existing <= MIN_IMAGES) {
            throw new InvalidImageCountException("En azi 2 sekil olmalidir");
        }
        var image = imageRepository.findByIdAndAnimalId(imageId, animalId).orElseThrow(
                () -> new ImageNotFoundException("Sekil tapilmadi")
        );
        imageRepository.delete(image);
        imageStorageService.delete(image.getPath());
    }

    private void saveImages(UUID animalId, List<MultipartFile> images) {
        List<String> paths = imageStorageService.saveAll(images);
        for (int i = 0; i < paths.size(); i++) {
            imageRepository.save(AnimalImageEntity.builder()
                    .animalId(animalId)
                    .path(paths.get(i))
                    .position(i)
                    .build());
        }
    }

    private List<String> buildImageUrls(UUID animalId) {
        return imageRepository.findAllByAnimalIdOrderByPosition(animalId).stream()
                .map(img -> "/api/v1/animal/" + animalId + "/image/" + img.getId())
                .toList();
    }

    public AnimalPageResponse getAnimals(
            String type,
            String breed,
            String city,
            AnimalGender gender,
            AnimalStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        var newStatus = status == null ? AnimalStatus.ACTIVE : status;
        Specification<AnimalEntity> specification = Specification.allOf(
                AnimalSpecification.hasBreed(breed),
                AnimalSpecification.hasCity(city),
                AnimalSpecification.hasGender(gender),
                AnimalSpecification.hasType(type),
                AnimalSpecification.hasStatus(newStatus),
                AnimalSpecification.notDeleted(),
                AnimalSpecification.priceBetween(minPrice, maxPrice)
        );
        Pageable pageable = PageRequest.of(page, size, resolveSort(sortBy, sortDir));
        Page<AnimalResponseDto> result = repository.findAll(specification, pageable)
                .map(mapper :: entityToDto);

        return AnimalPageResponse.builder()
                .content(result.getContent())
                .totalElements((int) result.getTotalElements())
                .totalPages(result.getTotalPages())
                .size(result.getSize())
                .build();
    }

    private Sort resolveSort(String sortBy, String sortDir) {
        String property = sortBy == null ? "createdAt" : sortBy;
        if (!ALLOWED_SORT_FIELDS.contains(property)) {
            property = "createdAt";
        }

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, property);
    }

    public AnimalResponseDto updateAnimal(UUID id, UUID userId, AnimalRequestDto dto) {
        var animal = repository.findById(id).orElseThrow(
                () -> new AnimalNotFoundException("Heyvan Tapilmadi")
        );
        checkOwnership(animal, userId);
        checkNotDeleted(animal);
        boolean priceChanged = dto.getPrice() != null && !dto.getPrice().equals(animal.getPrice());
        if (dto.getAge() != null) animal.setAge(dto.getAge());
        if (dto.getCity() != null) animal.setCity(dto.getCity());
        if (dto.getBreed() != null) animal.setBreed(dto.getBreed());
        if (dto.getDescription() != null) animal.setDescription(dto.getDescription());
        if (dto.getGender() != null) animal.setGender(dto.getGender());
        if (dto.getName() != null) animal.setName(dto.getName());
        if (dto.getPrice() != null) animal.setPrice(dto.getPrice());
        if (dto.getType() != null) animal.setType(dto.getType());
        if (dto.getStatus() != null) animal.setStatus(dto.getStatus());

        var saved = repository.save(animal);
        if (priceChanged) {
            notificationProducer.send(NotificationEvent.builder()
                    .eventType("ANIMAL_PRICE_CHANGED")
                    .targetUserId(userId)
                    .animalId(saved.getId())
                    .title("Qiymet deyisdi")
                    .message("'" + saved.getName() + "' elaninin qiymeti yenilendi: " + saved.getPrice())
                    .build());
        }
        return mapper.entityToDto(saved);
    }

    public void deleteAnimal(UUID id, UUID userId) {
        var animal = repository.findById(id).orElseThrow(
                () -> new AnimalNotFoundException("Heyvan Tapilmadi")
        );
        checkOwnership(animal, userId);
        checkNotDeleted(animal);
        animal.setStatus(AnimalStatus.DELETED);
        repository.save(animal);
//        repository.deleteById(animal.getId());
    }

    private void checkOwnership(AnimalEntity animal, UUID userId) {
        if (!animal.getUserId().equals(userId)) {
            throw new ForbiddenException();
        }
    }

    private void checkNotDeleted(AnimalEntity animal) {
        if (animal.getStatus() == AnimalStatus.DELETED) {
            throw new AnimalNotFoundException("Heyvan Tapilmadi");
        }
    }

    public UserContactResponseDto fetchSeller(UUID userId){
        if (userId == null) return null;
        try{
            return userClient.getUserContact(userId);
        } catch (Exception e) {
            return null;
        }
    }
}
