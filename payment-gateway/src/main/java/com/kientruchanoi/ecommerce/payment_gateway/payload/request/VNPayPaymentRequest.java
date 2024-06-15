package com.kientruchanoi.ecommerce.payment_gateway.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class VNPayPaymentRequest {
    private long amount;
    private String bankCode;
}
