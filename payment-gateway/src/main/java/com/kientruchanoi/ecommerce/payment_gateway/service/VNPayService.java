package com.kientruchanoi.ecommerce.payment_gateway.service;

import com.kientruchanoi.ecommerce.payment_gateway.payload.request.VNPayPaymentRequest;
import com.kientruchanoi.ecommerce.payment_gateway.payload.response.VNPayPaymentResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface VNPayService {
    VNPayPaymentResponse payment(HttpServletRequest request, VNPayPaymentRequest body);
}
