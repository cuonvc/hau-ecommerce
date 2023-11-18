package com.kientruchanoi.hauecommerce.service;

import com.kientruchanoi.hauecommerce.payload.dto.CategoryDto;
import com.kientruchanoi.hauecommerce.payload.repsonse.BaseResponse;
import com.kientruchanoi.hauecommerce.payload.repsonse.CategoryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

public interface CategoryService {
    ResponseEntity<BaseResponse<CategoryResponse>> create(CategoryDto categoryDto);

    ResponseEntity<BaseResponse<CategoryResponse>> update(CategoryDto categoryDto);

    ResponseEntity<BaseResponse<CategoryResponse>> getById(String id);

//    ResponseEntity<BaseResponse<PageResponseCategory>> getAllActive(Integer pageNo, Integer pageSize, String sortBy, String sortDir);
//
//    ResponseEntity<BaseResponse<PageResponseCategory>> getAll(Integer pageNo, Integer pageSize, String sortBy, String sortDir);

    ResponseEntity<BaseResponse<String>> delete(String id);

    ResponseEntity<BaseResponse<CategoryDto>> restore(String id);
}
