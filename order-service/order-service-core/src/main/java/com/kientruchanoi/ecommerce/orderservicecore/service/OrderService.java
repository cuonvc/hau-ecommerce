package com.kientruchanoi.ecommerce.orderservicecore.service;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.request.OrderRequest;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.OrderResponse;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.OrderResponseDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {
    ResponseEntity<BaseResponse<List<OrderResponseDetail>>> create(OrderRequest request);

    ResponseEntity<BaseResponse<OrderResponseDetail>> detail(String id);

    ResponseEntity<BaseResponse<List<OrderResponseDetail>>> listByOwner(String status, String type);

    ResponseEntity<BaseResponse<String>> cancel(String id);

    ResponseEntity<BaseResponse<OrderResponseDetail>> accept(String id);

    ResponseEntity<BaseResponse<OrderResponseDetail>> reject(String id);

    ResponseEntity<BaseResponse<OrderResponseDetail>> delivering(String id);

    ResponseEntity<BaseResponse<OrderResponseDetail>> done(String id);
}
