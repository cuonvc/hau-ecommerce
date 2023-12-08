package com.kientruchanoi.ecommerce.orderservicecore.controller;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.orderservicecore.service.OrderService;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.request.OrderRequest;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class CustomerController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<OrderResponse>> create(@RequestBody OrderRequest request) {
        return orderService.create(request);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BaseResponse<OrderResponse>> update(@PathVariable("id") String id,
                                                              @RequestBody OrderRequest request) {
        return orderService.update(id,request);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<BaseResponse<String>> cancel(@PathVariable("id") String id) {
        return orderService.cancel(id);
    }
}
