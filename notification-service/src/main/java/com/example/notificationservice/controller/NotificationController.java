package com.example.notificationservice.controller;

import com.example.notificationservice.dao.entity.NotificationEntity;
import com.example.notificationservice.exception.LoginRequiredException;
import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping
    public ResponseEntity<List<NotificationEntity>> getNotifications(
            @RequestHeader(value = "User-Id", required = false) UUID userId) {
        if (userId == null) {
            throw new LoginRequiredException();
        }
        return ResponseEntity.ok(service.getNotifications(userId));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID id,
            @RequestHeader(value = "User-Id", required = false) UUID userId) {
        if (userId == null) {
            throw new LoginRequiredException();
        }
        service.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}