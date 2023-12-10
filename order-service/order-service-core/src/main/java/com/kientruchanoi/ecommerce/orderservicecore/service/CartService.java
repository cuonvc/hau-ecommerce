package com.kientruchanoi.ecommerce.orderservicecore.service;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Cart;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.CartResponse;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CartService {

    ResponseEntity<BaseResponse<CartResponse>> addItem(String productId, int quantity);

    ResponseEntity<BaseResponse<CartResponse>> deleteItem(String productId);

    ResponseEntity<BaseResponse<Integer>> sizeOfCart();

    ResponseEntity<BaseResponse<CartResponse>> listItem();
}
