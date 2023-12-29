package com.kientruchanoi.ecommerce.notificationservicecore.service.impl;

import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import com.kientruchanoi.ecommerce.notificationservicecore.configuration.CustomUserDetail;
import com.kientruchanoi.ecommerce.notificationservicecore.entity.Notification;
import com.kientruchanoi.ecommerce.notificationservicecore.exception.APIException;
import com.kientruchanoi.ecommerce.notificationservicecore.exception.ResourceNotFoundException;
import com.kientruchanoi.ecommerce.notificationservicecore.payload.PushNotification;
import com.kientruchanoi.ecommerce.notificationservicecore.payload.PushNotificationResponse;
import com.kientruchanoi.ecommerce.notificationservicecore.repository.NotificationRepository;
import com.kientruchanoi.ecommerce.notificationservicecore.service.NotificationService;
import com.kientruchanoi.ecommerce.notificationserviceshare.payload.kafka.NotificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final ResponseFactory responseFactory;
    private final RestTemplate restTemplate;

    private static final String EXPO_PUSH_NOTI_URL = "https://exp.host/--/api/v2/push/send";

    @Override
    public ResponseEntity<BaseResponse<Notification>> markRead(String id) {
        Notification notification = repository.findByIdAndRecipient(id, getCurrentUserId())
                .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, "Thông báo không tồn tại"));
        notification.setSeen(true);
        return responseFactory.success("Success", repository.save(notification));
    }

    @Override
    public void markReadAll() {
//        try {
//            NotificationType notificationType = NotificationType.valueOf(type);
//            repository.updateAllSeenByUserId(getCurrentUserId(), notificationType.name());
//        } catch (Exception e) {
//            throw new APIException(HttpStatus.BAD_REQUEST, "Type khong hpo le");
//        }
        List<Notification> list = repository.findAllByRecipient(getCurrentUserId())
                        .stream().map(n -> {
                            n.setSeen(true);
                            return n;
                 }).toList();
        repository.saveAll(list);
    }

    @Override
    public ResponseEntity<BaseResponse<List<Notification>>> getAll() {
        List<Notification> notifications = repository.findAllByRecipient(getCurrentUserId());
        return responseFactory.success("Success", notifications);
    }

    @Override
    public void create(NotificationBuilder request) {
        request.getRecipients().forEach(userId -> {
            Notification notification = repository.save(Notification.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .type(request.getType().name())
                    .recipient(userId)
                    .seen(false)
                    .createdDate(LocalDateTime.now())
                    .build()
            );

            pushNotification(notification);
        });
    }

    private void pushNotification(Notification notification) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        log.info("TIGGERRRR - {}", 1);

        HttpEntity<PushNotification> entity = new HttpEntity<>(PushNotification.builder()
                .to(getDeviceToken(notification.getRecipient()))
                .sound("default")
                .title(notification.getTitle())
                .body(notification.getContent())
                .data(notification)
                .build(), headers);

        log.info("TIGGERRRR - {}", entity);

        RestTemplate externalRequest = new RestTemplate();
        ResponseEntity<String> response = externalRequest.postForEntity(EXPO_PUSH_NOTI_URL, entity, String.class);

        log.info("TRIGGERRR - {}", response);

        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();
            System.out.println("Response: " + responseBody);
        } else {
            System.err.println("Error: " + response.getStatusCodeValue());
        }
    }

    private String getDeviceToken(String userId) {
        return Optional
                .ofNullable(restTemplate.exchange(
                        "http://AUTH-SERVICE/api/auth/internal/device_token?userId=" + userId,  //http or https???
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<String>() {}
                ).getBody())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private CustomUserDetail getCurrentUser() {
        return (CustomUserDetail) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private String getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
