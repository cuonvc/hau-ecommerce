package com.kientruchanoi.ecommerce.orderservicecore.service.impl;

import com.kientruchanoi.ecommerce.authserviceshare.payload.response.DeliveryAddressResponse;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import com.kientruchanoi.ecommerce.orderservicecore.configuration.CustomUserDetail;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Cart;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Order;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Wallet;
import com.kientruchanoi.ecommerce.orderservicecore.exception.APIException;
import com.kientruchanoi.ecommerce.orderservicecore.exception.ResourceNotFoundException;
import com.kientruchanoi.ecommerce.orderservicecore.mapper.OrderMapper;
import com.kientruchanoi.ecommerce.orderservicecore.repository.CartRepository;
import com.kientruchanoi.ecommerce.orderservicecore.repository.OrderRepository;
import com.kientruchanoi.ecommerce.orderservicecore.repository.WalletRepository;
import com.kientruchanoi.ecommerce.orderservicecore.service.*;
import com.kientruchanoi.ecommerce.orderservicecore.utils.Constants;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.OrderStatus;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.PaymentStatus;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.PaymentType;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.TransactionType;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kientruchanoi.ecommerce.orderservicecore.utils.Constants.HttpMessage.ACCESS_DENIED;
import static com.kientruchanoi.ecommerce.orderservicecore.utils.Constants.OrderStatus.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ResponseFactory responseFactory;
    private final CommonService commonService;
    private final CartRepository cartRepository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final CartService cartService;

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<List<OrderResponse>>> create(OrderRequest request) {
        Cart cart = validCartMapProduct(request.getCartId(), request.getProductIds());

        List<ProductResponse> products = request.getProductIds().stream()
                .map(commonService::getProductInfo)
                .toList();

        String currentUserId = commonService.getCurrentUserId();

        //validate
        products.stream().forEach(p -> {
            if (p.getRemaining() < 1) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm đã hết.");
            } else if (p.getRemaining() < cart.getProductMapQuantity().get(p.getId())) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm không đủ");
            } else if (p.getUser().getId().equals(currentUserId)) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Bạn không thể tự tăng traffic sản phẩm của mình =))");
            } else if (!request.getPaymentType().equals(PaymentType.WALLET)
                    && !request.getPaymentType().equals(PaymentType.CASH)) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Phương thức thanh toán không hợp lệ");
            }
        });

        Double amount = 0d;
        for (ProductResponse p : products) {
            amount += (cart.getProductMapQuantity().get(p.getId()) * p.getStandardPrice());
        }

        if (request.getPaymentType().equals(PaymentType.WALLET)) {
            if (walletRepository.findByUserId(currentUserId)
                    .orElse(walletService.walletBuilder(currentUserId)).getBalance() < amount) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Số dư không đủ");
            }
        }


        //save orders
        List<OrderResponse> responses = products.stream().map(p -> {
            try {
                DeliveryAddressResponse deliverySource = commonService.getDeliveryInfo(request.getDeliverySourceId());
                DeliveryAddressResponse deliveryDestination = commonService.getDeliveryInfo(request.getDeliveryDestinationId());

                Order order = orderRepository.save(Order.builder()
                        .productId(p.getId())
                        .amount(p.getStandardPrice() * cart.getProductMapQuantity().get(p.getId()))
                        .sellerId(p.getUser().getId())
                        .customerId(currentUserId)
                        .sourceInfo(deliverySource)
                        .destinationInfo(deliveryDestination)
                        .createdDate(LocalDateTime.now())
                        .status(Status.ACTIVE.name())
                        .orderStatus(OrderStatus.PENDING.name())
                        .paymentType(request.getPaymentType().name())
                        .paymentStatus(PaymentStatus.UNPAID.name())
                        .quantity(cart.getProductMapQuantity().get(p.getId()))
                        .build());

                return orderMapper.entityToResponse(order);
            } catch (APIException exception) {
                throw new APIException(HttpStatus.BAD_REQUEST, exception.getMessage());
            }
        }).toList();

        //change balance for customer
        if (request.getPaymentType().equals(PaymentType.WALLET)) {
            walletService.reduceBalanceByOrder(currentUserId, amount, responses.stream().map(o -> o.getId()).toList());
        }

        //create transactions for customer (one transaction map to many order)
        transactionService.create(
                TransactionType.BUY,
                "Mua hàng",
                responses.stream().map(OrderResponse::getId).toList(),
                walletRepository.findByUserId(currentUserId).get().getBalance(), amount);

        //delete cart
        cartService.deleteProducts(currentUserId);

        return responseFactory.success(ORDER_CREATE_SUCCESS, responses);
    }

    private Cart validCartMapProduct(String cartId, List<String> productIds) {
        Cart cart = cartRepository.findByIdAndUserId(cartId, commonService.getCurrentUserId())
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng của bạn."));

        productIds.forEach(pId -> {
            if (!cart.getProductMapQuantity().containsKey(pId)) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Xảy ra lỗi, mặt hàng không có trong giỏ.");
            }
        });

        return cart;
    }

//    @Override
//    public ResponseEntity<BaseResponse<OrderResponse>> update(String id, OrderRequest request) {
//        String currentUserId = commonService.getCurrentUserId();
//
//        Order order = orderRepository.findByIdAndStatusAndCustomerId(id, Status.ACTIVE.name(), currentUserId)
//                .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, ORDER_NOT_FOUND));
//
//        ProductResponse product = commonService.getProductInfo(order.getProductId());
//        if (product.getRemaining() < 1) {
//            throw new APIException(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm đã hết.");
//        } else if (product.getRemaining() < request.getQuantity()) {
//            throw new APIException(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm không đủ");
//        }
//
//        order.setDestinationAddress(request.getDestinationAddress());
//        order.setNote(request.getNote());
//        order.setQuantity(request.getQuantity());
//        order.setAmount(product.getStandardPrice() * request.getQuantity());
//
//        return responseFactory.success("Success", orderMapper.entityToResponse(orderRepository.save(order)));
//    }
//
    @Override
    public ResponseEntity<BaseResponse<OrderResponseDetail>> detail(String id) {
        Order order = orderRepository.findByIdAndStatus(id, Status.ACTIVE.name())
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, ORDER_NOT_FOUND));

        String currentUserId = commonService.getCurrentUserId();
        if (!currentUserId.equals(order.getCustomerId())
                && currentUserId.equals(order.getSellerId())
                && commonService.getCurrentUser().getGrantedAuthorities().get(0).equals(Constants.USER_ROLE)) {
            throw new APIException(HttpStatus.UNAUTHORIZED, ACCESS_DENIED);
        }

        OrderResponseDetail detail = orderMapper.entityToResponseDetail(order);
        detail.setCustomer(commonService.getUserInfo(order.getCustomerId()));
        detail.setSeller(commonService.getUserInfo(order.getSellerId()));
        detail.setProduct(commonService.getProductInfo(order.getProductId()));

        return responseFactory.success("Success", detail);
    }
//
//    @Override
//    public ResponseEntity<BaseResponse<String>> cancel(String id) {
//        String currentUserId = commonService.getCurrentUserId();
//
//        Order order = orderRepository.findByIdAndStatusAndCustomerId(id, Status.ACTIVE.name(), currentUserId)
//                .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, ORDER_NOT_FOUND));
//
//        if (!order.getOrderStatus().equals(OrderStatus.PENDING.name())) {
//            throw new APIException(HttpStatus.BAD_REQUEST, ORDER_CANCEL_FAILED);
//        }
//
//        order.setOrderStatus(OrderStatus.CANCEL.name());
//        orderRepository.save(order);
//        return responseFactory.success("Sucess", ORDER_CANCEL_SUCCESS);
//    }
//
//    @Override
//    public ResponseEntity<BaseResponse<String>> accept(String id) {
//        return sellerAction(id,
//                Status.ACTIVE,
//                OrderStatus.PENDING,
//                ORDER_CANNOT_ACCEPT,
//                OrderStatus.ACCEPTED,
//                ORDER_ACCEPTED);
//    }
//
//    @Override
//    public ResponseEntity<BaseResponse<String>> reject(String id) {
//        return sellerAction(id,
//                Status.ACTIVE,
//                OrderStatus.PENDING,
//                ORDER_CANNOT_REJECT,
//                OrderStatus.REJECTED,
//                ORDER_REJECTED);
//    }
//
//    private ResponseEntity<BaseResponse<String>> sellerAction(String orderId, Status objectStatus, OrderStatus currentOrderStatus,
//                                                              String throwMessage, OrderStatus targetStatus, String responseMessage) {
//        String currentUserId = commonService.getCurrentUserId();
//
//        Order order = orderRepository.findByIdAndStatusAndSellerId(orderId, objectStatus.name(), currentUserId)
//                .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, ORDER_NOT_FOUND));
//
//        if (!order.getOrderStatus().equals(currentOrderStatus.name())) {
//            throw new APIException(HttpStatus.BAD_REQUEST, throwMessage);
//        }
//
//        order.setOrderStatus(targetStatus.name());
//        orderRepository.save(order);
//        return responseFactory.success("Success", responseMessage);
//    }

}
