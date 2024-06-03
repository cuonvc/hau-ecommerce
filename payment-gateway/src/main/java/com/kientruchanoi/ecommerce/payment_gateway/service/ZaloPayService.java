package com.kientruchanoi.ecommerce.payment_gateway.service;


import com.kientruchanoi.ecommerce.payment_gateway.payload.request.ZpPaymentRequest;
import com.kientruchanoi.ecommerce.payment_gateway.payload.response.ZaloGetListBankResponse;
import com.kientruchanoi.ecommerce.payment_gateway.payload.response.ZpPaymentResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface ZaloPayService {
    List<ZaloGetListBankResponse> getListBank() throws URISyntaxException, IOException;
    ZpPaymentResponse createOrder(ZpPaymentRequest request) throws IOException;
}
