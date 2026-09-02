package com.example.notificationservice.consumer;

import com.example.notificationservice.dto.NotificationEvent;
import com.example.notificationservice.dao.entity.NotificationType;
import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService service;

    @RabbitListener(queues = "email-notification-queue")
    public void handleEmail(NotificationEvent event) {
        log.info("[EMAIL] To: {} | {}", event.getTargetUserId(), event.getMessage());
        service.save(event, NotificationType.EMAIL);
    }

    @RabbitListener(queues = "sms-notification-queue")
    public void handleSms(NotificationEvent event) {
        log.info("[SMS] To: {} | {}", event.getTargetUserId(), event.getMessage());
        service.save(event, NotificationType.SMS);
    }

    @RabbitListener(queues = "push-notification-queue")
    public void handlePush(NotificationEvent event) {
        log.info("[PUSH] To: {} | {}", event.getTargetUserId(), event.getMessage());
        service.save(event, NotificationType.PUSH);
    }
}