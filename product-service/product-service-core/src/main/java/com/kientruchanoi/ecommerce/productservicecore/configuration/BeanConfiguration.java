package com.kientruchanoi.ecommerce.productservicecore.configuration;

import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.Query;
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

//    @Bean
//    public Query query() {
//        return new Query();
//    }
}
