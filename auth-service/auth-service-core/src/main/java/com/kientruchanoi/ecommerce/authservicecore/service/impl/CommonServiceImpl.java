package com.kientruchanoi.ecommerce.authservicecore.service.impl;

import com.kientruchanoi.ecommerce.authservicecore.config.CustomUserDetail;
import com.kientruchanoi.ecommerce.authservicecore.entity.DeviceToken;
import com.kientruchanoi.ecommerce.authservicecore.exception.ResourceNotFoundException;
import com.kientruchanoi.ecommerce.authservicecore.repository.DeviceTokenRepository;
import com.kientruchanoi.ecommerce.authservicecore.repository.UserRepository;
import com.kientruchanoi.ecommerce.authservicecore.service.CommonService;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.notificationserviceshare.payload.kafka.NotificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.kientruchanoi.ecommerce.authservicecore.service.impl.UserServiceImpl.ORDER_NOTIFY_ACTION;
import static com.kientruchanoi.ecommerce.authservicecore.util.Constant.FirebaseData.BODY;
import static com.kientruchanoi.ecommerce.authservicecore.util.Constant.FirebaseData.TITLE;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommonServiceImpl implements CommonService {

    private final RestTemplate restTemplate;
    private final StreamBridge streamBridge;
    private final DeviceTokenRepository deviceTokenRepository;

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

    @Override
    public String getDeviceToken(String userId) {
        return deviceTokenRepository.findByUserId(userId)
                .orElse(new DeviceToken()).getToken();
    }

    @Override
    public void sendNotification(Map<String, String> firebaseData, String recipient) {
        if (!firebaseData.get(TITLE).isEmpty() && !firebaseData.get(BODY).isEmpty()) {
            Message<NotificationBuilder> notiMessage = MessageBuilder.withPayload(
                            NotificationBuilder.builder()
                                    .recipient(recipient)
                                    .deviceToken(getDeviceToken(recipient))
                                    .firebaseData(firebaseData)
                                    .build()
                    ).setHeader(KafkaHeaders.KEY, recipient.getBytes())
                    .build();
            log.info("STARTING SEND MESSAGE... - {}", notiMessage);
            streamBridge.send(ORDER_NOTIFY_ACTION, notiMessage);
        }
    }
}
