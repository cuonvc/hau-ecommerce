package com.kientruchanoi.ecommerce.authservicecore.config;

import com.kientruchanoi.ecommerce.authserviceshare.payload.request.RegRequest;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfiguration {

    @Bean
    public RedisTemplate<RegRequest, String> redisTemplateObject(RedisConnectionFactory factory) {
        RedisTemplate<RegRequest, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        return template;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplateString(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        return template;
    }

    @Bean
    public ResponseFactory responseFactory() {
        return new ResponseFactory();
    }

//    @Bean
//    public RedisCacheConfiguration cacheConfiguration() {
//        return RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(15))
//                .disableCachingNullValues()
//                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
//    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
