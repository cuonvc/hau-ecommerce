package com.kientruchanoi.ecommerce.notificationservicecore.kafkaHandle;

import com.kientruchanoi.ecommerce.notificationservicecore.service.NotificationService;
import com.kientruchanoi.ecommerce.notificationserviceshare.payload.kafka.NotificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class OrderNotificationHandler {

    private final NotificationService notificationService;

    @Bean
    public Consumer<Message<NotificationBuilder>> orderNotificationHandle() {
        return message -> {
            log.info("KEYYY - {}", message.getHeaders().get(KafkaHeaders.RECEIVED_KEY));
            log.info("PAYLOAD - {}", message.getPayload());

            notificationService.create(message.getPayload());
        };
    }
}
