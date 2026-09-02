package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationEvent;
import com.example.notificationservice.dao.entity.NotificationEntity;
import com.example.notificationservice.dao.entity.NotificationType;
import com.example.notificationservice.dao.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    @Transactional
    public void save(NotificationEvent event, NotificationType type) {
        if (event.getTargetUserId() == null) {
            return;
        }
        repository.save(NotificationEntity.builder()
                .userId(event.getTargetUserId())
                .type(type)
                .title(event.getTitle())
                .message(event.getMessage())
                .isRead(false)
                .build());
    }

    @Transactional(readOnly = true)
    public List<NotificationEntity> getNotifications(UUID userId) {
        return repository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void markAsRead(UUID id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification tapilmadi"));
        entity.setRead(true);
        repository.save(entity);
    }
}