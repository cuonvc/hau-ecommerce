package com.kientruchanoi.ecommerce.orderservicecore.service.impl;

import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.orderservicecore.configuration.CustomUserDetail;
import com.kientruchanoi.ecommerce.orderservicecore.exception.ResourceNotFoundException;
import com.kientruchanoi.ecommerce.orderservicecore.service.CommonService;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResponse;
import lombok.RequiredArgsConstructor;
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
    public ProductResponse getProductInfo(String id) {
        return Optional.ofNullable(
                Objects.requireNonNull(restTemplate.exchange(
                        "http://PRODUCT-SERVICE/api/internal/product/" + id,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<BaseResponse<ProductResponse>>() {
                        }
                ).getBody()).getData()
        ).orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    @Override
    public UserResponse getUserInfo(String userId) {
        return Optional.ofNullable(
                Objects.requireNonNull(restTemplate.exchange(
                        "http://AUTH-SERVICE/api/auth/account/" + userId,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<BaseResponse<UserResponse>>() {
                        }
                ).getBody()).getData()
        ).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
