package com.kientruchanoi.ecommerce.orderservicecore.service;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.request.OrderRequest;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.OrderResponse;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.OrderResponseDetail;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    ResponseEntity<BaseResponse<OrderResponse>> create(OrderRequest request);

    ResponseEntity<BaseResponse<OrderResponse>> update(String id, OrderRequest request);

    ResponseEntity<BaseResponse<OrderResponseDetail>> detail(String id);

    ResponseEntity<BaseResponse<String>> cancel(String id);

    ResponseEntity<BaseResponse<String>> accept(String id);

    ResponseEntity<BaseResponse<String>> reject(String id);
}
