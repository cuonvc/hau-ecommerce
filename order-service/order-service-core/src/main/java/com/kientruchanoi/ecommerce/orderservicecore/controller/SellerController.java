package com.kientruchanoi.ecommerce.orderservicecore.controller;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.orderservicecore.service.OrderService;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.OrderResponseDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class SellerController {

    private final OrderService orderService;

    @PutMapping("/accept/{id}")
    public ResponseEntity<BaseResponse<OrderResponseDetail>> accept(@PathVariable("id") String id) {
        return orderService.accept(id);
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<BaseResponse<OrderResponseDetail>> reject(@PathVariable("id") String id) {
        return orderService.reject(id);
    }

    @PutMapping("/delivering/{id}")
    public ResponseEntity<BaseResponse<OrderResponseDetail>> delivering(@PathVariable("id") String id) {
        return orderService.delivering(id);
    }
}
