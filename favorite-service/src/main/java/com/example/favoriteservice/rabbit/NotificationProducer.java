package com.example.favoriteservice.rabbit;

import com.example.favoriteservice.dto.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbit.exchange:pet-market-exchange}")
    private String exchange;

    private static final Map<String, List<String>> CHANNEL_BY_EVENT = Map.of(
            "FAVORITE_ADDED", List.of("push")
    );

    public void send(NotificationEvent event) {
        List<String> channels = CHANNEL_BY_EVENT.getOrDefault(event.getEventType(), List.of());
        if (channels.isEmpty()) {
            log.warn("Kanal teyin olunmayib, event gonderilmedi: {}", event.getEventType());
            return;
        }
        for (String channel : channels) {
            String routingKey = "notification." + channel + "." + event.getEventType().toLowerCase();
            log.debug("Publishing event [{}] to [{}]", routingKey, exchange);
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
        }
    }
}