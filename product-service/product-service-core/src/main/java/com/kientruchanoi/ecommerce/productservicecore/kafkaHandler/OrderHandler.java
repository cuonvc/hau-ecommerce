package com.kientruchanoi.ecommerce.productservicecore.kafkaHandler;

import com.kientruchanoi.ecommerce.productservicecore.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class OrderHandler {

    private final ProductService productService;

    @Bean
    public Consumer<Message<Integer>> reduceQuantity() {
        return request -> {
            String productId = new String((byte[]) request.getHeaders().get(KafkaHeaders.RECEIVED_KEY));
            log.info("Trigger ID - {}, quantity - {}", productId, request.getPayload());

            productService.reduceQuantity(productId, request.getPayload());
        };
    }
}
