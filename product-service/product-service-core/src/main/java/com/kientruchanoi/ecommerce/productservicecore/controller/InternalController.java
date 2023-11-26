package com.kientruchanoi.ecommerce.productservicecore.controller;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.productservicecore.service.ProductService;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class InternalController {

    private final ProductService productService;

    @GetMapping("/product/{id}")
    public ResponseEntity<BaseResponse<ProductResponse>> getDetail(@PathVariable("id") String id) {
        return productService.getById(id);
    }
}
