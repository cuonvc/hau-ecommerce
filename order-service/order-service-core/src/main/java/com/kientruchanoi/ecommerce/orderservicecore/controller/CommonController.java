package com.kientruchanoi.ecommerce.orderservicecore.controller;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.orderservicecore.service.OrderService;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.OrderStatus;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.OrderResponseDetail;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class CommonController {

    private final OrderService orderService;

    @GetMapping("/detail/{id}")
    public ResponseEntity<BaseResponse<OrderResponseDetail>> orderDetail(@PathVariable("id") String id) {
        return orderService.detail(id);
    }

    @GetMapping("/owner/list")
    public ResponseEntity<BaseResponse<List<OrderResponseDetail>>> listByOwner(@RequestParam(value = "status", required = false) String status,
                                                                               @RequestParam(value = "type") String type) {
        return orderService.listByOwner(status, type.trim().toUpperCase());
    }
}
