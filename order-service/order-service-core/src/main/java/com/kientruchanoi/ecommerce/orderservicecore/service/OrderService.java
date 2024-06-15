package com.kientruchanoi.ecommerce.orderservicecore.service;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.orderservicecore.response.OrderCreatedResponse;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.request.OrderRequest;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.OrderResponseDetail;
import com.kientruchanoi.ecommerce.payment_gateway.payload.response.ZpPaymentResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {
    ResponseEntity<BaseResponse<OrderCreatedResponse>> create(OrderRequest request);

    ResponseEntity<BaseResponse<OrderResponseDetail>> detail(String id);

    ResponseEntity<BaseResponse<List<OrderResponseDetail>>> listByOwner(String status, String type);

    ResponseEntity<BaseResponse<String>> cancel(String id);

    ResponseEntity<BaseResponse<OrderResponseDetail>> accept(String id);

    ResponseEntity<BaseResponse<String>> acceptMultiple(List<String> ids);

    ResponseEntity<BaseResponse<OrderResponseDetail>> reject(String id);

    ResponseEntity<BaseResponse<OrderResponseDetail>> delivering(String id);

    ResponseEntity<BaseResponse<OrderResponseDetail>> done(String id);
}
