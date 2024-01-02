package com.kientruchanoi.ecommerce.orderservicecore.kafkaHandle;

import com.kientruchanoi.ecommerce.notificationserviceshare.payload.kafka.NotificationBuilder;
import com.kientruchanoi.ecommerce.orderservicecore.repository.CartRepository;
import com.kientruchanoi.ecommerce.orderservicecore.repository.WalletRepository;
import com.kientruchanoi.ecommerce.orderservicecore.service.CartService;
import com.kientruchanoi.ecommerce.orderservicecore.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class OrderNotificationHandler {

    private final WalletService walletService;
    private final WalletRepository walletRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;

    public Consumer<Message<NotificationBuilder>> orderNotificationHandle() {
        return message -> {
            String userId = new String((byte[]) message.getHeaders().get(KafkaHeaders.RECEIVED_KEY));

            if (walletRepository.findByUserId(userId).isEmpty()
                    && cartRepository.findByUserId(userId).isEmpty()) {

                walletRepository.save(walletService.walletBuilder(userId));
                cartService.initCart(userId);
            }
        };
    }
}
