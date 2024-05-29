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

            NotificationBuilder value = redisTemplate.opsForValue().get(recipient); //tránh bị lặp thông báo khi server restart
            //trường hợp đối với tạo tài khoản (có 1 noti nên dùng chung với topic của order luôn) thì
            //nếu như tạo lần thứ hai trở đi với cùng 1 tài khoản -> FLUSHALL in Redis để tiếp tục
            if (value == null ||
                    !value.getDeviceToken().equals(message.getPayload().getDeviceToken()) ||
                    !value.getRecipient().equals(message.getPayload().getRecipient()) ||
                    !value.getFirebaseData().equals(message.getPayload().getFirebaseData())
            ) {
                log.info("CREATING...");
                redisTemplate.opsForValue().set(recipient, message.getPayload());
                notificationService.create(message.getPayload());
            }
        };
    }
}
