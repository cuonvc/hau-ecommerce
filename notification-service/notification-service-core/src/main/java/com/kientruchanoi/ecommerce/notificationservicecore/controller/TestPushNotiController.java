package com.kientruchanoi.ecommerce.notificationservicecore.controller;

import com.google.firebase.messaging.*;
import com.kientruchanoi.ecommerce.notificationservicecore.service.NotificationService;
import com.kientruchanoi.ecommerce.notificationservicecore.service.impl.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.kientruchanoi.ecommerce.notificationservicecore.util.Constant.FirebaseData.BODY;
import static com.kientruchanoi.ecommerce.notificationservicecore.util.Constant.FirebaseData.TITLE;

@RestController
@RequestMapping("/api/test/push")
@RequiredArgsConstructor
public class TestPushNotiController {

    private final NotificationServiceImpl notificationService;

    @PostMapping
    public ResponseEntity<String> pushNotification(@RequestBody PushRequest request) {
        try {
            return ResponseEntity.ok(notificationService.pushNotification(request.deviceToken, request.data));
        } catch (Exception e) {
            System.out.println("PUSH NOTIFICATION ERROR - " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public static class PushRequest {
        public String deviceToken;
        public Map<String, String> data;
    }
}
