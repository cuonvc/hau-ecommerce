package com.kientruchanoi.ecommerce.payment_gateway.controller;

import com.kientruchanoi.ecommerce.payment_gateway.payload.request.ZaloPayCallbackRequest;
import com.kientruchanoi.ecommerce.payment_gateway.service.ZaloPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//payment bên thứ 3 (zalopay, momo,...) sẽ request vào api này để trả trạng thái thanh toán
@RestController
@RequestMapping("/api/payment/callback")
@RequiredArgsConstructor
@Slf4j
public class CallbackController {

    private final ZaloPayService zaloPayService;

    @PostMapping("/zalopay")
    public String receiveZpOrderStatus(String jsonString) {
        return zaloPayService.receiveOrderStatus(jsonString);
    }

    @GetMapping("/vnpay")
    public ResponseEntity<?> callback(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        log.info("---------> TRIGGERRR - {}", status);
        if (status.equals("00")) {
            log.info("VNPay callback success - {}", request.getPathInfo());
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
