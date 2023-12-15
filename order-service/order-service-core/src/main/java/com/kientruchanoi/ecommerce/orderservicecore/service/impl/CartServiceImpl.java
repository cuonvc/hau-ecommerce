package com.kientruchanoi.ecommerce.orderservicecore.service.impl;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Cart;
import com.kientruchanoi.ecommerce.orderservicecore.exception.APIException;
import com.kientruchanoi.ecommerce.orderservicecore.repository.CartRepository;
import com.kientruchanoi.ecommerce.orderservicecore.service.CartService;
import com.kientruchanoi.ecommerce.orderservicecore.service.CommonService;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.CartResponse;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.ProductMapQuantity;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.CacheResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CommonService commonService;
    private final ResponseFactory responseFactory;

    @Override
    public ResponseEntity<BaseResponse<CartResponse>> addItem(String productId, int quantity) {
        String currentUserId = commonService.getCurrentUserId();
        Cart cart = cartRepository.findByUserId(currentUserId)
                .orElse(Cart.builder()
                        .userId(currentUserId)
                        .productMapQuantity(new HashMap<>())
                        .build()
                );

        ProductResponse product = commonService.getProductInfo(productId); //checking product id
        if (quantity < 1) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm phải lớn hơn 0");
        } else if (product.getRemaining() < 1) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm đã hết.");
        } else if (product.getRemaining() < quantity) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm không đủ");
        } else if (product.getUser().getId().equals(currentUserId)) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Bạn không thể tự tăng traffic cho sản phẩm của mình =))");
        }

        cart.getProductMapQuantity().put(productId, quantity);
        return responseFactory.success("Success", buildResponse(cartRepository.save(cart)));
    }

    @Override
    public ResponseEntity<BaseResponse<CartResponse>> deleteItem(String productId) {
        Cart cart = cartRepository.findByUserId(commonService.getCurrentUserId())
                .orElseThrow(() -> new APIException(HttpStatus.UNAUTHORIZED, "Không được phép truy cập."));

        if (!cart.getProductMapQuantity().containsKey(productId)) {
            throw new APIException(HttpStatus.NOT_FOUND, "Mặt hàng không có trong giỏ");
        }


        Map<String, Integer> pmq = cart.getProductMapQuantity();
        log.info("BEFORE ====== > - {}", pmq);
        pmq.remove(productId);
        log.info("AFTER ====== > - {}", pmq);
        cart.setProductMapQuantity(pmq);
        return responseFactory.success("Success", buildResponse(cartRepository.save(cart)));
    }

    @Override
    public ResponseEntity<BaseResponse<Integer>> sizeOfCart() {
        Cart cart = cartRepository.findByUserId(commonService.getCurrentUserId())
                .orElse(Cart.builder()
                        .userId(commonService.getCurrentUserId())
                        .productMapQuantity(new HashMap<>())
                        .build());
        cartRepository.save(cart);

        return responseFactory.success("Success", cart.getProductMapQuantity().size());
    }

    @Override
    public ResponseEntity<BaseResponse<CartResponse>> listItem() {
        Cart cart = cartRepository.findByUserId(commonService.getCurrentUserId())
                .orElse(Cart.builder()
                        .userId(commonService.getCurrentUserId())
                        .productMapQuantity(new HashMap<>())
                        .build());
        cartRepository.save(cart);

        return responseFactory.success("Success", buildResponse(cart));
    }

    private CartResponse buildResponse(Cart cart) {
        List<ProductMapQuantity> responseList = new ArrayList<>();
        cart.getProductMapQuantity().forEach((key, value) -> responseList.add(
                ProductMapQuantity.builder()
                        .product(commonService.getProductInfo(key))
                        .quantity(value)
                        .build()
                )
        );

        return CartResponse.builder()
                .id(cart.getId())
                .user(commonService.getUserInfo(cart.getUserId()))
                .items(responseList)
                .build();
    }

    @Override
    public void deleteProducts(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, "Lỗi hệ thống, không thể xoá giỏ hàng"));

        cart.setProductMapQuantity(new HashMap<>());
        cartRepository.save(cart);
    }
}
