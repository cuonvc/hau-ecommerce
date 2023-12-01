package com.kientruchanoi.ecommerce.productservicecore.service;

import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.productservicecore.entity.Product;
import com.kientruchanoi.ecommerce.productserviceshare.payload.request.ProductRequest;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.PageResponseProduct;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResponse;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface ProductService {
    ResponseEntity<BaseResponse<ProductResponse>> create(ProductRequest request);

    ResponseEntity<BaseResponse<ProductResponse>> update(String id, ProductRequest request);

    ResponseEntity<BaseResponse<ProductResponse>> getById(String id);

    ResponseEntity<BaseResponse<String>> deleteById(String id);

    ResponseEntity<BaseResponse<ProductResponse>> restore(String id);

    ResponseEntity<BaseResponse<PageResponseProduct>> findAllByActive(Integer pageNo, Integer pageSize, String sortBy, String sortDir);

    ResponseEntity<BaseResponse<PageResponseProduct>> findAll(Integer pageNo, Integer pageSize, String sortBy, String sortDir);

    ResponseEntity<BaseResponse<PageResponseProduct>> findAllByOwner(Integer pageNo, Integer pageSize, String sortBy, String sortDir);

    ResponseEntity<BaseResponse<PageResponseProduct>> findAllByShop(String userId, Integer pageNo, Integer pageSize, String sortBy, String sortDir);

//    ResponseEntity<BaseResponse<String>> delete(String id);
//
//    ResponseEntity<BaseResponse<ProductResponse>> restore(String id);
}
