package com.kientruchanoi.ecommerce.orderservicecore.controller;

import com.kientruchanoi.ecommerce.orderservicecore.entity.VietQrApi;
import com.kientruchanoi.ecommerce.orderservicecore.service.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;

    @GetMapping("/vietqr")
    public ResponseEntity<?> getVietQr() {
        VietQrApi response = systemService.getVietQr();
        return ResponseEntity.ok(systemService.getVietQr());
    }
}
