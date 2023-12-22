package com.kientruchanoi.ecommerce.notificationservicecore.service.impl;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import com.kientruchanoi.ecommerce.notificationservicecore.configuration.CustomUserDetail;
import com.kientruchanoi.ecommerce.notificationservicecore.entity.Notification;
import com.kientruchanoi.ecommerce.notificationservicecore.exception.APIException;
import com.kientruchanoi.ecommerce.notificationservicecore.repository.NotificationRepository;
import com.kientruchanoi.ecommerce.notificationservicecore.service.NotificationService;
import com.kientruchanoi.ecommerce.notificationserviceshare.payload.kafka.NotificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final ResponseFactory responseFactory;

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
            repository.save(Notification.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .type(request.getType().name())
                    .recipient(userId)
                    .seen(false)
                    .createdDate(LocalDateTime.now())
                    .build()
            );
        });
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
