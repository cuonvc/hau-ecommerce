package com.kientruchanoi.ecommerce.orderservicecore.service.impl;

import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import com.kientruchanoi.ecommerce.orderservicecore.configuration.CustomUserDetail;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Order;
import com.kientruchanoi.ecommerce.orderservicecore.exception.APIException;
import com.kientruchanoi.ecommerce.orderservicecore.exception.ResourceNotFoundException;
import com.kientruchanoi.ecommerce.orderservicecore.mapper.OrderMapper;
import com.kientruchanoi.ecommerce.orderservicecore.repository.OrderRepository;
import com.kientruchanoi.ecommerce.orderservicecore.service.OrderService;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.OrderStatus;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.PaymentStatus;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.PaymentType;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.request.OrderRequest;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.OrderResponse;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.OrderResponseDetail;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final RestTemplate restTemplate;
    private final ResponseFactory responseFactory;

    @Override
    public ResponseEntity<BaseResponse<OrderResponse>> create(OrderRequest request) {
        ProductResponse product = getProductInfo(request.getProductId());
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (product.getRemaining() < 1) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm đã hết.");
        } else if (product.getRemaining() < request.getQuantity()) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm không đủ");
        } else if (product.getUser().getId().equals(userDetail.getId())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Bạn không thể tự tăng traffic sản phẩm của mình =))");
        }

        Order order = orderRepository.save(Order.builder()
                        .productId(product.getId())
                        .amount(product.getStandardPrice() * request.getQuantity())
                        .sellerId(product.getUser().getId())
                        .customerId(userDetail.getId())
                        .sourceAddress(getUserInfo(product.getUser().getId()).getDetailAddress())
                        .destinationAddress(request.getDestinationAddress())
                        .createdDate(LocalDateTime.now())
                        .status(Status.ACTIVE.name())
                        .orderStatus(OrderStatus.PENDING.name())
                        .paymentType(PaymentType.CASH.name())
                        .paymentStatus(PaymentStatus.UNPAID.name())
                        .quantity(product.getRemaining())
                .build());

        return responseFactory.success("Tạo đơn hàng thành công.", orderMapper.entityToResponse(order));
    }

    @Override
    public ResponseEntity<BaseResponse<OrderResponse>> update(String id, OrderRequest request) {
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Order order = orderRepository.findByIdAndStatusAndCustomerId(id, Status.ACTIVE.name(), userDetail.getId())
                .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST,
                        "Đơn hàng không tồn tại hoặc bạn không có quyền truy cập"));

        ProductResponse product = getProductInfo(order.getProductId());
        if (product.getRemaining() < 1) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm đã hết.");
        } else if (product.getRemaining() < request.getQuantity()) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm không đủ");
        }

        order.setDestinationAddress(request.getDestinationAddress());
        order.setNote(request.getNote());
        order.setQuantity(request.getQuantity());
        order.setAmount(product.getStandardPrice() * request.getQuantity());

        return responseFactory.success("Success", orderMapper.entityToResponse(orderRepository.save(order)));
    }

    @Override
    public ResponseEntity<BaseResponse<OrderResponseDetail>> detail(String id) {
        Order order = orderRepository.findByIdAndStatus(id, Status.ACTIVE.name())
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Đơn hàng không tồn tại..."));

        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        if (!userDetail.getId().equals(order.getCustomerId())
                && userDetail.getId().equals(order.getSellerId())
                && userDetail.getGrantedAuthorities().get(0).equals("USER")) {
            throw new APIException(HttpStatus.UNAUTHORIZED, "Không được phép truy cập");
        }

        OrderResponseDetail detail = orderMapper.entityToResponseDetail(order);
        detail.setCustomer(getUserInfo(order.getCustomerId()));
        detail.setSeller(getUserInfo(order.getSellerId()));
        detail.setProduct(getProductInfo(order.getProductId()));

        return responseFactory.success("Success", detail);
    }

    @Override
    public ResponseEntity<BaseResponse<String>> cancel(String id) {
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Order order = orderRepository.findByIdAndStatusAndCustomerId(id, Status.ACTIVE.name(), userDetail.getId())
                .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST,
                        "Đơn hàng không tồn tại hoặc không có quyền truy cập"));

        if (!order.getOrderStatus().equals(OrderStatus.PENDING.name())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Không thể huỷ đơn.");
        }

        order.setOrderStatus(OrderStatus.CANCEL.name());
        orderRepository.save(order);
        return responseFactory.success("Sucess", "Huỷ đơn hàng thành công.");
    }

    private ProductResponse getProductInfo(String id) {
        return Optional.ofNullable(
                Objects.requireNonNull(restTemplate.exchange(
                        "http://PRODUCT-SERVICE/api/internal/product/" + id,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<BaseResponse<ProductResponse>>() {
                        }
                ).getBody()).getData()
        ).orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    private UserResponse getUserInfo(String userId) {
        return Optional.ofNullable(
                Objects.requireNonNull(restTemplate.exchange(
                        "http://AUTH-SERVICE/api/auth/account/" + userId,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<BaseResponse<UserResponse>>() {
                        }
                ).getBody()).getData()
        ).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
