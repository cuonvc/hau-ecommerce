package com.kientruchanoi.ecommerce.payment_gateway.controller;

import com.kientruchanoi.ecommerce.payment_gateway.payload.request.ZaloPayCallbackRequest;
import com.kientruchanoi.ecommerce.payment_gateway.service.ZaloPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//payment bên thứ 3 (zalopay, momo,...) sẽ request vào api này để trả trạng thái thanh toán
@RestController
@RequestMapping("/api/payment/callback")
@RequiredArgsConstructor
public class CallbackController {

    private final ZaloPayService zaloPayService;

    @PostMapping("/zalopay")
    public String receiveZpOrderStatus(String jsonString) {
        return zaloPayService.receiveOrderStatus(jsonString);
    }
}
