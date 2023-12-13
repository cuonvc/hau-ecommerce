package com.kientruchanoi.ecommerce.orderservicecore.service;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Transaction;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.PageResponseTransaction;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.TransactionResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TransactionService {

    ResponseEntity<BaseResponse<TransactionResponse>> detail(String id);

    ResponseEntity<BaseResponse<List<TransactionResponse>>> byOwner();

    ResponseEntity<BaseResponse<PageResponseTransaction>> allByAdmin(Integer pageNo, Integer pageSize, String sortBy, String sortDir);
}
