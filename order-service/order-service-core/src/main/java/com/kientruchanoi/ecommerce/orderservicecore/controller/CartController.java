package com.kientruchanoi.ecommerce.orderservicecore.controller;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Cart;
import com.kientruchanoi.ecommerce.orderservicecore.service.CartService;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.CartResponse;
import jakarta.ws.rs.POST;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/insert")
    public ResponseEntity<BaseResponse<CartResponse>> insert(@RequestParam("productId") String productId,
                                                             @RequestParam("quantity") int quantity) {
        return cartService.addItem(productId, quantity);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<BaseResponse<CartResponse>> deleteItem(@PathVariable("productId") String productId) {
        return cartService.deleteItem(productId);
    }

    @GetMapping("/items/quantity")
    public ResponseEntity<BaseResponse<Integer>> getSizeOfCart() {
        return cartService.sizeOfCart();
    }

    @GetMapping("/owner")
    public ResponseEntity<BaseResponse<CartResponse>> getByOwner() {
        return cartService.listItem();
    }
}
