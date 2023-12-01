//package com.kientruchanoi.ecommerce.productservicecore.kafkaHandler;
//
//import com.kientruchanoi.ecommerce.baseservice.payload.response.FileObjectResponse;
//import com.kientruchanoi.ecommerce.productservicecore.service.ProductResourceService;
//import com.kientruchanoi.ecommerce.productservicecore.service.ProductService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.support.KafkaHeaders;
//import org.springframework.messaging.Message;
//
//import java.util.function.Consumer;
//
//@Configuration
//@RequiredArgsConstructor
//@Slf4j
//public class FileHandler {
//
//    private final ProductResourceService resourceService;
//    private final ProductService productService;
//
//    @Bean
//    public Consumer<Message<FileObjectResponse>> productImageProcess() {
//        return response -> {
//            String key = new String((byte[]) response.getHeaders().get(KafkaHeaders.RECEIVED_KEY));
//            log.info("triggerr - key - {} - value - {}", key, response.getPayload());
//
//            resourceService.storeImagePath(key, response.getPayload().getField(), response.getPayload().getUrl());
//
////            FileObjectResponse objectResponse = response.getPayload();
//        };
//    }
//
//    @Bean
//    public Consumer<Message<FileObjectResponse>> categoryImageProcess() {
//        return response -> {
//            String key = new String((byte[]) response.getHeaders().get(KafkaHeaders.RECEIVED_KEY));
//            log.info("Trigger category - {} - {}", key, response.getPayload());
//
//            resourceService.storeImagePath(key, response.getPayload().getField(), response.getPayload().getUrl());
//        };
//    }
//
//}
