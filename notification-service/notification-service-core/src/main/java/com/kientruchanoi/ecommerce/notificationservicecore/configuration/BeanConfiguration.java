package com.kientruchanoi.ecommerce.notificationservicecore.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import com.kientruchanoi.ecommerce.notificationserviceshare.payload.kafka.NotificationBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
public class BeanConfiguration {

    @Bean
    public ResponseFactory responseFactory() {
        return new ResponseFactory();
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RedisTemplate<String, NotificationBuilder> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, NotificationBuilder> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        return template;
    }

    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource("firebase.json").getInputStream());
        FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .build();
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(firebaseOptions);
        return FirebaseMessaging.getInstance(firebaseApp);
    }

//    @Bean
//    public Query query() {
//        return new Query();
//    }
}