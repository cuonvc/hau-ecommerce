package com.kientruchanoi.ecommerce.payment_gateway.controller;

import com.kientruchanoi.ecommerce.payment_gateway.payload.request.VNPayPaymentRequest;
import com.kientruchanoi.ecommerce.payment_gateway.payload.request.ZpPaymentRequest;
import com.kientruchanoi.ecommerce.payment_gateway.service.VNPayService;
import com.kientruchanoi.ecommerce.payment_gateway.service.ZaloPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final ZaloPayService zaloPayService;
    private final VNPayService vnPayService;

    @PostMapping("/zalopay")
    public ResponseEntity<?> paymentZalopay(@RequestBody ZpPaymentRequest request) {
        try {
            return ResponseEntity.ok(zaloPayService.createOrder(request));
        } catch (IOException e) {
            return new ResponseEntity<>("Lỗi thanh toán", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/vnpay")
    public ResponseEntity<?> payment(HttpServletRequest request, @RequestBody VNPayPaymentRequest body) {
        return ResponseEntity.ok(vnPayService.payment(request, body));
    }
}
