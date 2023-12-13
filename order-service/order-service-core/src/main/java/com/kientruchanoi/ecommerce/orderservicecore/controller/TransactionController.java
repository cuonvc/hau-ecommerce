package com.kientruchanoi.ecommerce.orderservicecore.controller;

import com.kientruchanoi.ecommerce.baseservice.constant.PageConstant;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Transaction;
import com.kientruchanoi.ecommerce.orderservicecore.service.TransactionService;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.PageResponseTransaction;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/transaction/detail/{id}")
    public ResponseEntity<BaseResponse<TransactionResponse>> detail(@PathVariable("id") String id) {
        return transactionService.detail(id);
    }

    @GetMapping("/transaction/owner")
    public ResponseEntity<BaseResponse<List<TransactionResponse>>> listByOwner() {
        return transactionService.byOwner();
    }

    @GetMapping("/moderator/transactions")
    public ResponseEntity<BaseResponse<PageResponseTransaction>> all(@RequestParam(value = "pageNo",
                                                                             defaultValue = PageConstant.PAGE_NO, required = false) Integer pageNo,
                                                                     @RequestParam(value = "pageSize",
                                                                             defaultValue = PageConstant.PAGE_SIZE, required = false) Integer pageSize,
                                                                     @RequestParam(value = "sortBy",
                                                                             defaultValue = PageConstant.SORT_BY, required = false) String sortBy,
                                                                     @RequestParam(value = "sortDir",
                                                                             defaultValue = PageConstant.SORT_DIR, required = false) String sortDir) {

        return transactionService.allByAdmin(pageNo, pageSize, sortBy, sortDir);
    }
}
