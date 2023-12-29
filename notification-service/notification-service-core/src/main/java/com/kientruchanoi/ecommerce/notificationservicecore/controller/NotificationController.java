package com.kientruchanoi.ecommerce.notificationservicecore.controller;

import com.kientruchanoi.ecommerce.baseservice.constant.PageConstant;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.notificationservicecore.entity.Notification;
import com.kientruchanoi.ecommerce.notificationservicecore.service.NotificationService;
import com.kientruchanoi.ecommerce.notificationserviceshare.payload.response.PageResponseNotification;
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

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<PageResponseNotification>> getAll(@RequestParam(value = "pageNo",
                                                                                defaultValue = PageConstant.PAGE_NO, required = false) Integer pageNo,
                                                                         @RequestParam(value = "pageSize",
                                                                                 defaultValue = PageConstant.PAGE_SIZE, required = false) Integer pageSize,
                                                                         @RequestParam(value = "sortBy",
                                                                                 defaultValue = PageConstant.SORT_BY, required = false) String sortBy,
                                                                         @RequestParam(value = "sortDir",
                                                                                 defaultValue = PageConstant.SORT_DIR, required = false) String sortDir) {
        return service.getAll(pageNo, pageSize, sortBy, sortDir);
    }
}
