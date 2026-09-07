package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationEvent;
import com.example.notificationservice.dao.entity.NotificationEntity;
import com.example.notificationservice.dao.entity.NotificationType;
import com.example.notificationservice.dao.repository.NotificationRepository;
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
    public boolean markAsRead(UUID id, UUID userId) {
        return repository.findById(id)
                .filter(n -> n.getUserId().equals(userId))
                .map(n -> {
                    n.setRead(true);
                    repository.save(n);
                    return true;
                })
                .orElse(false);
    }
}