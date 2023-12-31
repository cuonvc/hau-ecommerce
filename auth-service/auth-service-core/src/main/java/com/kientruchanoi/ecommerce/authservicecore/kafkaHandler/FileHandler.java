//package com.kientruchanoi.ecommerce.authservicecore.kafkaHandler;
//
//import com.kientruchanoi.ecommerce.authservicecore.service.UserService;
//import com.kientruchanoi.ecommerce.baseservice.payload.response.FileObjectResponse;
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
//    private final UserService userService;
//
//    @Bean
//    public Consumer<Message<FileObjectResponse>> avatarProcess() {
//        return response -> {
//            log.info("Triggerrrrrrr - {}", response.getPayload());
//            String userId = new String((byte[]) response.getHeaders().get(KafkaHeaders.RECEIVED_KEY));
//            FileObjectResponse value = response.getPayload();
//
//            userService.saveChangeImage(userId, value.getField(), value.getUrl());
//        };
//    }
//}
