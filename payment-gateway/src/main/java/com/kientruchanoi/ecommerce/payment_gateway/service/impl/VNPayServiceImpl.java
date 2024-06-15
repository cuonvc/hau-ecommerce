package com.kientruchanoi.ecommerce.payment_gateway.service.impl;

import com.kientruchanoi.ecommerce.payment_gateway.config.VNPayConfig;
import com.kientruchanoi.ecommerce.payment_gateway.payload.request.VNPayPaymentRequest;
import com.kientruchanoi.ecommerce.payment_gateway.payload.response.VNPayPaymentResponse;
import com.kientruchanoi.ecommerce.payment_gateway.service.VNPayService;
import com.kientruchanoi.ecommerce.payment_gateway.utils.BaseUtil;
import com.kientruchanoi.ecommerce.payment_gateway.utils.vnPay.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VNPayServiceImpl implements VNPayService {

    private final VNPayConfig vnPayConfig;

    @Override
    public VNPayPaymentResponse payment(HttpServletRequest request, VNPayPaymentRequest body) {
        long amount = body.getAmount() * 100L; //vì VNPay tự loại bỏ 2 số 0
        String bankCode = body.getBankCode();
        Map<String, String> vnpMapParam = vnPayConfig.getVNPayConfig();
        vnpMapParam.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpMapParam.put("vnp_BankCode", bankCode);
        }

        vnpMapParam.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        String queryUrl = VNPayUtil.getPaymentURL(vnpMapParam, true);
        String hashData = VNPayUtil.getPaymentURL(vnpMapParam, false);
        String vnpSecureHash = BaseUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);

        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String response =  vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return VNPayPaymentResponse.builder()
                .code("200")
                .message("SUCCESS")
                .paymentUrl(response)
                .build();
    }
}
