package com.kientruchanoi.ecommerce.notificationservicecore.kafkaHandle;

import com.kientruchanoi.ecommerce.notificationservicecore.service.NotificationService;
import com.kientruchanoi.ecommerce.notificationserviceshare.payload.kafka.NotificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class OrderNotificationHandler {

    private final NotificationService notificationService;
    private final RedisTemplate<String, NotificationBuilder> redisTemplate;

    @Bean
    public Consumer<Message<NotificationBuilder>> orderNotificationHandle() {
        return message -> {
            String recipient = new String((byte[]) message.getHeaders().get(KafkaHeaders.RECEIVED_KEY));
            log.info("KEYYY - {}", recipient);
            log.info("PAYLOAD - {}", message.getPayload());

//            notificationService.create(message.getPayload());
//            redisTemplate.opsForValue().set(recipient, message.getPayload());

            NotificationBuilder value = redisTemplate.opsForValue().get(recipient);
            if (value == null || !value.equals(message.getPayload())) {
                log.info("CREATING...");
                redisTemplate.opsForValue().set(recipient, message.getPayload());
                notificationService.create(message.getPayload());
            }
        };
    }
}
