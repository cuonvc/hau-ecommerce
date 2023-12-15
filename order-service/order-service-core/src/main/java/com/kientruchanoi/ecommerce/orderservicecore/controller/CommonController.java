package com.kientruchanoi.ecommerce.orderservicecore.controller;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.orderservicecore.service.OrderService;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.OrderResponseDetail;
import jakarta.ws.rs.GET;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class CommonController {

    private final OrderService orderService;

    @GetMapping("/detail/{id}")
    public ResponseEntity<BaseResponse<OrderResponseDetail>> orderDetail(@PathVariable("id") String id) {
        return orderService.detail(id);
    }
}
