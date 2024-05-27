package com.kientruchanoi.ecommerce.notificationservicecore.service.impl;

import com.google.firebase.messaging.*;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import com.kientruchanoi.ecommerce.notificationservicecore.configuration.CustomUserDetail;
import com.kientruchanoi.ecommerce.notificationservicecore.entity.Notification;
import com.kientruchanoi.ecommerce.notificationservicecore.exception.APIException;
import com.kientruchanoi.ecommerce.notificationservicecore.exception.ResourceNotFoundException;
import com.kientruchanoi.ecommerce.notificationservicecore.mapper.NotificationMapper;
import com.kientruchanoi.ecommerce.notificationservicecore.repository.NotificationRepository;
import com.kientruchanoi.ecommerce.notificationservicecore.service.NotificationService;
import com.kientruchanoi.ecommerce.notificationserviceshare.payload.kafka.NotificationBuilder;
import com.kientruchanoi.ecommerce.notificationserviceshare.payload.response.NotificationResponse;
import com.kientruchanoi.ecommerce.notificationserviceshare.payload.response.PageResponseNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kientruchanoi.ecommerce.notificationservicecore.util.Constant.FirebaseData.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final ResponseFactory responseFactory;
    private final RestTemplate restTemplate;
    private final NotificationMapper notificationMapper;

    private static final String EXPO_PUSH_NOTI_URL = "https://exp.host/--/api/v2/push/send";
    private final FirebaseMessaging firebaseMessaging;

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
    public ResponseEntity<BaseResponse<PageResponseNotification>> getAll(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Notification> notifications = repository.findAllByRecipient(getCurrentUserId(), pageable);
        return responseFactory.success("Success", paging(notifications));
    }

    private PageResponseNotification paging(Page<Notification> notifications) {
        List<NotificationResponse> notificationList = notifications.getContent()
                .stream().map(entity -> {
                    NotificationResponse response = notificationMapper.entityToResponse(entity);
                    response.setRecipients(List.of(entity.getRecipient()));
                    return response;
                })
                .sorted(Comparator.comparing(NotificationResponse::getCreatedDate).reversed())
                .collect(Collectors.toList());

        return (PageResponseNotification) PageResponseNotification.builder()
                .pageNo(notifications.getNumber())
                .pageSize(notificationList.size())
                .content(notificationList)
                .totalPages(notifications.getTotalPages())
                .totalItems((int) notifications.getTotalElements())
                .last(notifications.isLast())
                .build();
    }

    @Override
    public void create(NotificationBuilder request) {
        log.info("TRIGGERR NOTIFICATION CREATING - {}", request);
        Map<String, String> firebaseData = request.getFirebaseData();
        Notification notification = repository.save(Notification.builder()
                .title(firebaseData.get(TITLE))
                .content(firebaseData.get(BODY))
                .type(firebaseData.get(TYPE))
                .recipient(request.getRecipient())
                .seen(false)
                .createdDate(LocalDateTime.now())
                .build()
        );

        pushNotification(request.getDeviceToken(), request.getFirebaseData(), notification);
    }

    @Async
    public void pushNotification(String deviceToken, Map<String, String> data, Notification notification) {
        if (!data.get(TITLE).isEmpty() && !data.get(BODY).isEmpty()) {
            log.info("LOGGGGG PUSH NOTI - {} - {} - {}", deviceToken, data, notification);
            Message message = Message.builder()
                    .setToken(deviceToken)
                    .putAllData(data)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setContentAvailable(true)
                                    .build())
                            .build())
                    .build();
            firebaseMessaging.sendAsync(message);
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