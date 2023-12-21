package com.kientruchanoi.ecommerce.notificationservicecore.service;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.notificationservicecore.entity.Notification;
import com.kientruchanoi.ecommerce.notificationserviceshare.payload.kafka.NotificationBuilder;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface NotificationService {

    ResponseEntity<BaseResponse<Notification>> markRead(String id);

    void markReadAll();

    ResponseEntity<BaseResponse<List<Notification>>> getAll();

    void create(NotificationBuilder request);
}
