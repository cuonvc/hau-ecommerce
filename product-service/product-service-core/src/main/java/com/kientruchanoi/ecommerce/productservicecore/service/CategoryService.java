package com.kientruchanoi.ecommerce.productservicecore.service;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.productserviceshare.payload.CategoryDto;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.CategoryResponse;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.PageResponseCategory;
import org.springframework.http.ResponseEntity;

public interface CategoryService {
    ResponseEntity<BaseResponse<CategoryResponse>> create(CategoryDto categoryDto);

    ResponseEntity<BaseResponse<CategoryResponse>> update(CategoryDto categoryDto);

    ResponseEntity<BaseResponse<CategoryResponse>> getById(String id);

    ResponseEntity<BaseResponse<PageResponseCategory>> getAllActive(Integer pageNo, Integer pageSize, String sortBy, String sortDir);

    ResponseEntity<BaseResponse<PageResponseCategory>> getAll(Integer pageNo, Integer pageSize, String sortBy, String sortDir);

    ResponseEntity<BaseResponse<String>> delete(String id);

    ResponseEntity<BaseResponse<CategoryDto>> restore(String id);
}
