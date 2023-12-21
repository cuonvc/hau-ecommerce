package com.kientruchanoi.ecommerce.orderservicecore.service.impl;

import com.kientruchanoi.ecommerce.authserviceshare.payload.response.DeliveryAddressResponse;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.notificationserviceshare.enumerate.NotificationType;
import com.kientruchanoi.ecommerce.notificationserviceshare.payload.kafka.NotificationBuilder;
import com.kientruchanoi.ecommerce.orderservicecore.configuration.CustomUserDetail;
import com.kientruchanoi.ecommerce.orderservicecore.exception.APIException;
import com.kientruchanoi.ecommerce.orderservicecore.exception.ResourceNotFoundException;
import com.kientruchanoi.ecommerce.orderservicecore.service.CommonService;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    private final RestTemplate restTemplate;
    private final StreamBridge streamBridge;

    private static final String ORDER_NOTIFY_ACTION = "order.notify.action";

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
    public List<String> getAllAdminId() {
        return Optional.of(
                Objects.requireNonNull(restTemplate.exchange(
                        "http://AUTH-SERVICE/api/auth/internal/admins",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<String>>() {
                        }
                ).getBody()
        )).orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, "Lỗi hệ thống, không có admin nào"));
    }

    @Override
    public DeliveryAddressResponse getDeliveryInfo(String id) {
        DeliveryAddressResponse response = Optional.ofNullable(
                Objects.requireNonNull(restTemplate.exchange(
                        "http://AUTH-SERVICE/api/auth/delivery/view/" + id,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<BaseResponse<DeliveryAddressResponse>>() {
                        }
                ).getBody()).getData()
        ).orElseThrow(() -> new ResourceNotFoundException("Delivery information", "id", id));
        return response;
    }

    @Override
    public ProductResponse getProductInfo(String id) {
        ProductResponse response = Optional.ofNullable(
                Objects.requireNonNull(restTemplate.exchange(
                        "http://PRODUCT-SERVICE/api/internal/product/" + id,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<BaseResponse<ProductResponse>>() {
                        }
                ).getBody()).getData()
        ).orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        response.setUser(getUserInfo(response.getUser().getId()));
        return response;
    }

    @Override
    public ProductResponse getProductInfoNotUser(String productId) {
        ProductResponse response = getProductInfo(productId);
        response.setUser(null);
        return response;
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

    @Override
    public void sendNotification(NotificationType type, String message, List<String> recipients) {
        Message<NotificationBuilder> notiMessage = MessageBuilder.withPayload(
                NotificationBuilder.builder()
                        .type(type)
                        .title(type.getMessage())
                        .content(message)
                        .recipients(recipients)
                        .build()
        ).build();
        streamBridge.send(ORDER_NOTIFY_ACTION, notiMessage);
    }
}
