package com.kientruchanoi.ecommerce.orderservicecore.service.impl;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Cart;
import com.kientruchanoi.ecommerce.orderservicecore.exception.APIException;
import com.kientruchanoi.ecommerce.orderservicecore.repository.CartRepository;
import com.kientruchanoi.ecommerce.orderservicecore.service.CartService;
import com.kientruchanoi.ecommerce.orderservicecore.service.CommonService;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.CartResponse;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.CacheResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CommonService commonService;
    private final ResponseFactory responseFactory;

    @Override
    public ResponseEntity<BaseResponse<CartResponse>> addItem(String productId) {
        String currentUserId = commonService.getCurrentUserId();
        Cart cart = cartRepository.findByUserId(currentUserId)
                .orElse(Cart.builder()
                        .userId(currentUserId)
                        .productIds(new ArrayList<>())
                        .build());

        List<String> items = cart.getProductIds();
        items.add(productId);
        cart.setProductIds(items);
        return responseFactory.success("Success", buildResponse(cartRepository.save(cart)));
    }

    @Override
    public ResponseEntity<BaseResponse<CartResponse>> deleteItem(String productId) {
        Cart cart = cartRepository.findByUserId(commonService.getCurrentUserId())
                .orElseThrow(() -> new APIException(HttpStatus.UNAUTHORIZED, "Không được phép truy cập."));

        if (!cart.getProductIds().contains(productId)) {
            throw new APIException(HttpStatus.NOT_FOUND, "Mặt hàng không có trong giỏ");
        }

        List<String> updateItems = cart.getProductIds();
        updateItems.remove(productId);
        cart.setProductIds(updateItems);
        return responseFactory.success("Success", buildResponse(cartRepository.save(cart)));
    }

    @Override
    public ResponseEntity<BaseResponse<Integer>> sizeOfCart() {
        return responseFactory.success("Success",
                cartRepository.findByUserId(commonService.getCurrentUserId())
                        .orElse(new Cart())
                        .getProductIds().size());
    }

    @Override
    public ResponseEntity<BaseResponse<CartResponse>> listItem() {
        Cart cart = cartRepository.findByUserId(commonService.getCurrentUserId())
                .orElse(new Cart());

        return responseFactory.success("Success", buildResponse(cart));
    }

    private CartResponse buildResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .user(commonService.getUserInfo(cart.getUserId()))
                .products(cart.getProductIds().stream()
                        .map(commonService::getProductInfo)
                        .collect(Collectors.toList()))
                .build();
    }
}
