package com.kientruchanoi.ecommerce.notificationservicecore.configuration;

import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import com.kientruchanoi.ecommerce.notificationserviceshare.payload.kafka.NotificationBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

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

//    @Bean
//    public Query query() {
//        return new Query();
//    }
}
