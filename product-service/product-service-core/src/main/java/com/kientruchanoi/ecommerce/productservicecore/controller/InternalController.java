package com.kientruchanoi.ecommerce.productservicecore.controller;

import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import com.kientruchanoi.ecommerce.productservicecore.entity.Product;
import com.kientruchanoi.ecommerce.productservicecore.mapper.ProductMapper;
import com.kientruchanoi.ecommerce.productservicecore.repository.ProductRepository;
import com.kientruchanoi.ecommerce.productservicecore.service.ProductService;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class InternalController {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ResponseFactory responseFactory;

    @GetMapping("/product/{id}")
    public ResponseEntity<BaseResponse<ProductResponse>> getDetail(@PathVariable("id") String id) {
        Product product = productRepository.findByIdAndIsActive(id, Status.ACTIVE)
                        .orElse(null);

        if (product == null) {
            //return success để service caller không bị drop
            return responseFactory.success("Not found", null);
        }

        ProductResponse response = productMapper.entityToResponse(product);
        response.setUser(UserResponse.builder()
                        .id(product.getUserId())
                .build());
        return responseFactory.success("Success", response);
    }
}
