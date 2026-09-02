package com.example.animalservice.controller;

import com.example.animalservice.exception.LoginRequiredException;
import com.example.animalservice.service.ImageStorageService;
import com.example.animalservice.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/animal/{animalId}/image")
@RequiredArgsConstructor
public class ImageController {

    private final AnimalService animalService;
    private final ImageStorageService imageStorageService;

    @GetMapping("/{imageId}")
    public ResponseEntity<Resource> getImage(
            @PathVariable UUID animalId,
            @PathVariable UUID imageId) {
        var image = animalService.getImageByAnimalId(imageId, animalId);
        Resource resource = imageStorageService.load(image.getPath());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imageStorageService.contentType(image.getPath())))
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                .body(resource);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable UUID animalId,
            @PathVariable UUID imageId,
            @RequestHeader(value = "User-Id", required = false) UUID userId) {
        if (userId == null) {
            throw new LoginRequiredException();
        }
        animalService.deleteImage(animalId, userId, imageId);
        return ResponseEntity.ok().build();
    }

}