package com.kientruchanoi.ecommerce.payment_gateway.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class VNPayPaymentResponse {
    private String code;
    private String message;
    private String paymentUrl;
}
