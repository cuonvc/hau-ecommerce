package com.kientruchanoi.ecommerce.orderservicecore.service.impl;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Cart;
import com.kientruchanoi.ecommerce.orderservicecore.entity.CartProduct;
import com.kientruchanoi.ecommerce.orderservicecore.exception.APIException;
import com.kientruchanoi.ecommerce.orderservicecore.repository.CartProductRepository;
import com.kientruchanoi.ecommerce.orderservicecore.repository.CartRepository;
import com.kientruchanoi.ecommerce.orderservicecore.service.CartService;
import com.kientruchanoi.ecommerce.orderservicecore.service.CommonService;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.CartResponse;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.ProductMapQuantity;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.CacheResponse;
import java.time.LocalDateTime;
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
    private final CartProductRepository cartProductRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<CartResponse>> addItem(String productId, int quantity) {
        String currentUserId = commonService.getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();
        Cart cart = cartRepository.findByUserId(currentUserId)
                .orElse(Cart.builder()
                        .userId(currentUserId)
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

        CartProduct cartProduct = CartProduct.builder()
                .cartId(cart.getId())
                .productId(productId)
                .quantity(quantity)
                .createdAt(now)
                .updatedAt(now)
                .build();
        entityManager.persist(cartProduct);
        entityManager.persist(cart);
        return responseFactory.success("Success", buildResponse(cart, cartProduct));
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<CartResponse>> deleteItem(String productId) {
        Cart cart = cartRepository.findByUserId(commonService.getCurrentUserId())
                .orElseThrow(() -> new APIException(HttpStatus.UNAUTHORIZED, "Không được phép truy cập."));

        List<String> cartProductListId = cartProductRepository.findAllByCartId(cart.getId())
                .stream().map(CartProduct::getProductId)
                .toList();
        if (!cartProductListId.contains(productId)) {
            throw new APIException(HttpStatus.NOT_FOUND, "Mặt hàng không có trong giỏ");
        }
        cartProductRepository.deleteByCartAndProduct(cart.getId(), productId);
        entityManager.flush();
        return responseFactory.success("Success", buildResponse(cartRepository.save(cart), null));
    }

    @Override
    public ResponseEntity<BaseResponse<Integer>> sizeOfCart() {
        Cart cart = cartRepository.findByUserId(commonService.getCurrentUserId())
                .orElse(Cart.builder()
                        .userId(commonService.getCurrentUserId())
                        .build());
        cartRepository.save(cart);

        return responseFactory.success("Success", cartProductRepository.countProductInCart(cart.getId()));
    }

    @Override
    public ResponseEntity<BaseResponse<CartResponse>> listItem() {
        Cart cart = cartRepository.findByUserId(commonService.getCurrentUserId())
                .orElse(Cart.builder()
                        .userId(commonService.getCurrentUserId())
                        .build());
        cartRepository.save(cart);

        return responseFactory.success("Success", buildResponse(cart, null));
    }

    private CartResponse buildResponse(Cart cart, CartProduct newCartProduct) {
        List<ProductMapQuantity> responseList = new ArrayList<>();
        List<CartProduct> listInCart = cartProductRepository.findAllByCartId(cart.getId());
        if (newCartProduct != null) {
            listInCart.add(newCartProduct);
        }

        listInCart.forEach(item -> responseList.add(
                ProductMapQuantity.builder()
                        .product(commonService.getProductInfo(item.getProductId()))
                        .quantity(item.getQuantity())
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
    @Transactional
    public void deleteProducts(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, "Lỗi hệ thống, không thể clear giỏ hàng"));

        cartProductRepository.deleteAllByCart(cart.getId());
    }

    @Override
    public void initCart(String userId) {
        cartRepository.save(
                Cart.builder()
                        .userId(userId)
                        .build()
        );
    }
}
