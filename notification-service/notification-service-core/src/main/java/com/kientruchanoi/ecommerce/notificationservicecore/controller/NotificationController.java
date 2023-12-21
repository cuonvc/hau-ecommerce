package com.kientruchanoi.ecommerce.notificationservicecore.controller;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.notificationservicecore.entity.Notification;
import com.kientruchanoi.ecommerce.notificationservicecore.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PutMapping("/seen/{id}")
    public ResponseEntity<BaseResponse<Notification>> markRead(@PathVariable("id") String id) {
        return service.markRead(id);
    }

    @PutMapping("/seen/all")
    public void markReadAll() {
        service.markReadAll();
    }

    @PutMapping("/all")
    public ResponseEntity<BaseResponse<List<Notification>>> getAll() {
        return service.getAll();
    }
}
