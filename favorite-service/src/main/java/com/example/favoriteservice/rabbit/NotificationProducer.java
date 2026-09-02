package com.example.favoriteservice.rabbit;

import com.example.favoriteservice.dto.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbit.exchange:pet-market-exchange}")
    private String exchange;

    public void send(NotificationEvent event) {
        String routingKey = "notification." + event.getEventType().toLowerCase();
        log.debug("Publishing event [{}] to [{}]", routingKey, exchange);
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}