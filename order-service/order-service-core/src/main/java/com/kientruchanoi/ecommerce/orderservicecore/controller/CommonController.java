package com.kientruchanoi.ecommerce.orderservicecore.controller;

import com.kientruchanoi.ecommerce.baseservice.constant.PageConstant;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.orderservicecore.service.OrderService;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.OrderStatus;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.OrderResponseDetail;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.PageResponseOrder;
import jakarta.ws.rs.GET;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public ResponseEntity<BaseResponse<PageResponseOrder>> listByOwner(@RequestParam(value = "pageNo",
                                                                                        defaultValue = PageConstant.PAGE_NO, required = false) Integer pageNo,
                                                                       @RequestParam(value = "pageSize",
                                                                                       defaultValue = PageConstant.PAGE_SIZE, required = false) Integer pageSize,
                                                                       @RequestParam(value = "sortBy",
                                                                                       defaultValue = PageConstant.SORT_BY, required = false) String sortBy,
                                                                       @RequestParam(value = "sortDir",
                                                                                       defaultValue = PageConstant.SORT_DIR, required = false) String sortDir,
                                                                       @RequestParam(value = "status", required = false) String status,
                                                                       @RequestParam(value = "type") String type,
                                                                       @RequestParam(value = "from", required = false) LocalDateTime from,
                                                                       @RequestParam(value = "to", required = false) LocalDateTime to) {
        return orderService.listByOwner(pageNo, pageSize, sortBy, sortDir, status, type.trim().toUpperCase(), from, to);
    }

    @GetMapping("/admin/list")
    public ResponseEntity<BaseResponse<PageResponseOrder>> listByAdmin(@RequestParam(value = "pageNo",
                                                                                        defaultValue = PageConstant.PAGE_NO, required = false) Integer pageNo,
                                                                               @RequestParam(value = "pageSize",
                                                                                       defaultValue = PageConstant.PAGE_SIZE, required = false) Integer pageSize,
                                                                               @RequestParam(value = "sortBy",
                                                                                       defaultValue = PageConstant.SORT_BY, required = false) String sortBy,
                                                                               @RequestParam(value = "sortDir",
                                                                                       defaultValue = PageConstant.SORT_DIR, required = false) String sortDir,
                                                                               @RequestParam(value = "status", required = false) String status,
                                                                               @RequestParam(value = "from", required = false) LocalDateTime from,
                                                                               @RequestParam(value = "to", required = false) LocalDateTime to) {
        return orderService.listByAdmin(pageNo, pageSize, sortBy, sortDir, status, from, to);
    }
}
