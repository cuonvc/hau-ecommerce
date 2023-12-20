package com.kientruchanoi.ecommerce.authservicecore.service.impl;

import com.kientruchanoi.ecommerce.authservicecore.config.CustomUserDetail;
import com.kientruchanoi.ecommerce.authservicecore.exception.ResourceNotFoundException;
import com.kientruchanoi.ecommerce.authservicecore.service.CommonService;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    private final RestTemplate restTemplate;

    @Override
    public CustomUserDetail getCurrentUser() {
        return (CustomUserDetail) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    @Override
    public String getCurrentUserId() {
        return getCurrentUser().getId();
    }

    @Override
    public Integer getProductsByOwner() {
        return getProductsByUser(getCurrentUserId());
    }

    @Override
    public Integer getProductsByUser(String userId) {
        return Optional.ofNullable(
                Objects.requireNonNull(restTemplate.exchange(
                        "http://PRODUCT-SERVICE/api/internal/products/" + userId,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<BaseResponse<Integer>>() {
                        }
                ).getBody()).getData()
        ).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
