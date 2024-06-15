package com.kientruchanoi.ecommerce.orderservicecore.service.impl;

import com.kientruchanoi.ecommerce.authserviceshare.payload.response.DeliveryAddressResponse;
import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import com.kientruchanoi.ecommerce.notificationserviceshare.enumerate.NotificationType;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Cart;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Order;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Wallet;
import com.kientruchanoi.ecommerce.orderservicecore.exception.APIException;
import com.kientruchanoi.ecommerce.orderservicecore.mapper.OrderMapper;
import com.kientruchanoi.ecommerce.orderservicecore.repository.CartProductRepository;
import com.kientruchanoi.ecommerce.orderservicecore.repository.CartRepository;
import com.kientruchanoi.ecommerce.orderservicecore.repository.OrderRepository;
import com.kientruchanoi.ecommerce.orderservicecore.repository.WalletRepository;
import com.kientruchanoi.ecommerce.orderservicecore.response.OrderCreatedResponse;
import com.kientruchanoi.ecommerce.orderservicecore.service.*;
import com.kientruchanoi.ecommerce.orderservicecore.util.Constants;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.*;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.request.OrderRequest;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.OrderResponseDetail;
import com.kientruchanoi.ecommerce.payment_gateway.payload.request.ItemRequest;
import com.kientruchanoi.ecommerce.payment_gateway.payload.request.VNPayPaymentRequest;
import com.kientruchanoi.ecommerce.payment_gateway.payload.request.ZpPaymentRequest;
import com.kientruchanoi.ecommerce.payment_gateway.payload.response.VNPayPaymentResponse;
import com.kientruchanoi.ecommerce.payment_gateway.payload.response.ZpPaymentResponse;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.*;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kientruchanoi.ecommerce.orderservicecore.util.Constants.FirebaseData.*;
import static com.kientruchanoi.ecommerce.orderservicecore.util.Constants.HttpMessage.ACCESS_DENIED;
import static com.kientruchanoi.ecommerce.orderservicecore.util.Constants.OrderStatus.*;
import static com.kientruchanoi.ecommerce.orderserviceshare.enumerate.PaymentType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ResponseFactory responseFactory;
    private final CommonService commonService;
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final CartService cartService;
    private final StreamBridge streamBridge;
    private final RestTemplate restTemplate;
    private final EntityManager entityManager;

    private static final String ORDER_REDUCE_PRODUCT_QUANTITY = "order.reduce.prduct.quantity";
    private static final String ORDER_TYPE_SELL = "SELL";
    private static final String ORDER_TYPE_BUY = "BUY";

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<OrderCreatedResponse>> create(OrderRequest request) {
        Cart cart = validCartMapProduct(request.getCartId(), request.getProductIds());

        List<ProductResponse> products = request.getProductIds().stream()
                .map(commonService::getProductInfo)
                .toList();

        String currentUserId = commonService.getCurrentUserId();

        //validate
        products.stream().forEach(p -> {
            if (p.getRemaining() < 1) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm đã hết.");
            } else if (p.getRemaining() < cartProductRepository.countQuantityOfProductInCart(cart.getId(), p.getId())) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Số lượng sản phẩm không đủ");
            } else if (p.getUser().getId().equals(currentUserId)) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Bạn không thể tự tăng traffic sản phẩm của mình =))");
            } else if (!List.of(WALLET, CASH, ZALO_PAY, VN_PAY).contains(request.getPaymentType())) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Phương thức thanh toán không hợp lệ");
            }
        });

        Double amount = 0d;
        for (ProductResponse p : products) {
            amount += (cartProductRepository.countQuantityOfProductInCart(cart.getId(), p.getId()) * p.getStandardPrice());
        }

        if (request.getPaymentType().equals(WALLET)) {
            if (walletRepository.findByUserId(currentUserId)
                    .orElse(walletService.walletBuilder(currentUserId)).getBalance() < amount) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Số dư không đủ");
            }
        }


        //save orders
        List<OrderResponseDetail> responses = products.stream().map(p -> {
            try {
//                DeliveryAddressResponse deliverySource = commonService.getDeliveryInfo(request.getDeliverySourceId());
                DeliveryAddressResponse deliveryDestination = commonService.getDeliveryInfo(request.getDeliveryDestinationId());
                int quantity = cartProductRepository.countQuantityOfProductInCart(cart.getId(), p.getId());

                Order order = Order.builder()
                        .productId(p.getId())
                        .amount(p.getStandardPrice() * quantity)
                        .note(request.getNote())
                        .sellerId(p.getUser().getId())
                        .customerId(currentUserId)
                        .deliveryAddressId(deliveryDestination.getId())
                        .destinationInfo(deliveryDestination)
                        .createdDate(LocalDateTime.now())
                        .status(Status.ACTIVE.name())
                        .orderStatus(OrderStatus.PENDING.name())
                        .paymentType(request.getPaymentType().name())
                        .paymentStatus(PaymentStatus.UNPAID.name())
                        .quantity(quantity)
                        .build();
                entityManager.persist(order);

                return buildResponseDetail(order);
            } catch (APIException exception) {
                throw new APIException(HttpStatus.BAD_REQUEST, exception.getMessage());
            }
        }).toList();

        //change balance for customer
        if (request.getPaymentType().equals(WALLET)) {
            walletService.reduceBalanceByOrder(currentUserId, amount, responses.stream().map(o -> o.getId()).toList());

            //create transactions for customer (one transaction map to many order)
            transactionService.create(
                    TransactionType.BUY,
                    "Mua hàng",
                    responses.stream().map(OrderResponseDetail::getId).toList(),
                    walletRepository.findByUserId(currentUserId).get(), amount);
        }

        ZpPaymentResponse zalopayResponse = null;
        VNPayPaymentResponse vnPayResponse = null;
        if (request.getPaymentType().equals(ZALO_PAY)) {
            zalopayResponse = Objects.requireNonNull(restTemplate.postForEntity(
                    "http://PAYMENT-GATEWAY/api/payment/zalopay",
                    ZpPaymentRequest.builder()
                            .amount(responses.stream().map(OrderResponseDetail::getAmount).reduce(0, Integer::sum))
                            .itemRequest(new ItemRequest[]{new ItemRequest()})
                            .build(),
                    ZpPaymentResponse.class
            ).getBody());
        } else if (request.getPaymentType().equals(VN_PAY)) {
            ResponseEntity<?> responseEntity = Objects.requireNonNull(restTemplate.postForEntity(
                    "http://PAYMENT-GATEWAY/api/payment/vnpay",
                    VNPayPaymentRequest.builder()
                            .amount(responses.stream().map(OrderResponseDetail::getAmount).reduce(0, Integer::sum))
                            .bankCode("NCB")
                            .build(),
                    VNPayPaymentResponse.class
            ));
            vnPayResponse = (VNPayPaymentResponse) responseEntity.getBody();
        }

        //delete products in the cart
        cartService.deleteProducts(currentUserId);

        //push notification
        responses.forEach(o -> {
            Map<String, String> firebaseData = new HashMap<>();
            firebaseData.put(TYPE, NotificationType.ORDER_CREATED.name());
            firebaseData.put(TITLE, NotificationType.ORDER_CREATED.getMessage());
            firebaseData.put(BODY, "Bạn có đơn hàng " + o.getProduct().getName() + " mới.");
            commonService.sendNotification(firebaseData, o.getSeller().getId());
        });

        return switch (request.getPaymentType()) {
            case ZALO_PAY:
                yield responseFactory.success(ORDER_CREATE_SUCCESS, OrderCreatedResponse.builder().response(zalopayResponse).build());
            case VN_PAY:
                yield responseFactory.success(ORDER_CREATE_SUCCESS, OrderCreatedResponse.builder().response(vnPayResponse).build());
            case WALLET, CASH:
                yield responseFactory.success(ORDER_CREATE_SUCCESS, OrderCreatedResponse.builder().build());
            default:
                yield responseFactory.fail(HttpStatus.BAD_REQUEST, "Phương thức thanh toán sai", null);
        };
    }

    private Cart validCartMapProduct(String cartId, List<String> productIds) {
        Cart cart = cartRepository.findByIdAndUserId(cartId, commonService.getCurrentUserId())
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng của bạn."));

        productIds.forEach(pId -> {
            if (cartProductRepository.findByCartAndProduct(cart.getId(), pId).isEmpty()) {
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

        return responseFactory.success("Success", buildResponseDetail(order));
    }

    @Override
    public ResponseEntity<BaseResponse<List<OrderResponseDetail>>> listByOwner(String status, String type) {

        String currentUserId = commonService.getCurrentUserId();
        List<Order> orders = new ArrayList<>();

        switch (type) {
            case ORDER_TYPE_SELL -> {
                if (status == null) {
                    orders = orderRepository.findAllBySellerId(currentUserId);
                } else {
                    try {
                        OrderStatus orderStatus = OrderStatus.valueOf(status.trim().toUpperCase()); //check enum name ok
                        orders = orderRepository.findAllBySellerIdAndOrderStatus(currentUserId, orderStatus.name());
                    } catch (Exception exception) {
                        throw new APIException(HttpStatus.BAD_REQUEST, "Status khoông hợp lệ");
                    }
                }
            }
            case ORDER_TYPE_BUY -> {
                if (status == null) {
                    orders = orderRepository.findAllByCustomerId(currentUserId);
                } else {
                    try {
                        OrderStatus orderStatus = OrderStatus.valueOf(status.trim().toUpperCase()); //check enum name ok
                        orders = orderRepository.findAllByCustomerIdAndOrderStatus(currentUserId, orderStatus.name());
                    } catch (Exception exception) {
                        throw new APIException(HttpStatus.BAD_REQUEST, "Status khoông hợp lệ");
                    }
                }
            }
            default -> throw new APIException(HttpStatus.BAD_REQUEST, "Type không hợp lệ");
        }

        List<OrderResponseDetail> detailList = orders.stream()
                .map(this::buildResponseDetail)
                .sorted(Comparator.comparing(OrderResponseDetail::getCreatedDate).reversed())
                .collect(Collectors.toList());

//        detailList.sort(Comparator.comparing(OrderResponseDetail::getCreatedDate));
//        Collections.sort(detailList, (o1, o2) -> (o1.getCreatedDate().compareTo(o2.getCreatedDate())));
//        Collections.reverse(detailList);
        return responseFactory.success("Succes", detailList);
    }


    @Override
    public ResponseEntity<BaseResponse<String>> cancel(String id) {
        String currentUserId = commonService.getCurrentUserId();

        Order order = orderRepository.findByIdAndStatusAndCustomerId(id, Status.ACTIVE.name(), currentUserId)
                .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, ORDER_NOT_FOUND));

        if (!order.getOrderStatus().equals(OrderStatus.PENDING.name())) {
            throw new APIException(HttpStatus.BAD_REQUEST, ORDER_CANCEL_FAILED);
        }

        order.setOrderStatus(OrderStatus.CANCEL.name());
        orderRepository.save(order);

        //refund to wallet
        if (List.of(WALLET.name(), ZALO_PAY.name(), VN_PAY.name()).contains(order.getPaymentType())
                && order.getPaymentStatus().equals(PaymentStatus.PAID.name())) {
            Wallet wallet = walletService.customerRefund(order);
            transactionService.create(TransactionType.REFUND,
                    "Hoàn tiền cho do hàng bị huỷ", List.of(order.getId()), wallet, order.getAmount());

            //push notification
            Map<String, String> firebaseData = new HashMap<>();
            firebaseData.put(TYPE, NotificationType.WALLET_REFUND.name());
            firebaseData.put(TITLE, NotificationType.WALLET_REFUND.getMessage());
            firebaseData.put(BODY, "Hoàn tiền do đơn hàng " + order.getId() + " bị huỷ.");
            commonService.sendNotification(firebaseData, order.getCustomerId());
        }

        //push notification
        Map<String, String> firebaseData = new HashMap<>();
        firebaseData.put(TYPE, NotificationType.ORDER_CANCEL.name());
        firebaseData.put(TITLE, NotificationType.ORDER_CANCEL.getMessage());
        firebaseData.put(BODY, "Đơn hàng " + order.getId() + " đã bị huỷ.");
        commonService.sendNotification(firebaseData, order.getSellerId());

        return responseFactory.success("Đã huỷ đơn hàng " + order.getId(), ORDER_CANCEL_SUCCESS);
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<OrderResponseDetail>> accept(String id) {
        return responseFactory.success("Đã tiếp nhận đơn hàng", buildResponseDetail(baseAccept(id)));
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<String>> acceptMultiple(List<String> ids) {
        ids.forEach(this::baseAccept);
        return responseFactory.success("Đã tiếp nhận đơn hàng", "OK");
    }

    private Order baseAccept(String id) {
        Order order = sellerAction(id,
                Status.ACTIVE,
                OrderStatus.PENDING, //old status
                ORDER_CANNOT_ACCEPT,
                OrderStatus.ACCEPTED); //current status

        //reduce product quantity
        Message<Integer> message = MessageBuilder.withPayload(order.getQuantity())
                .setHeader(KafkaHeaders.KEY, order.getProductId().getBytes())
                .build();
        streamBridge.send(ORDER_REDUCE_PRODUCT_QUANTITY, message);

        //push notification
        Map<String, String> firebaseData = new HashMap<>();
        firebaseData.put(TYPE, NotificationType.ORDER_ACCEPTED.name());
        firebaseData.put(TITLE, NotificationType.ORDER_ACCEPTED.getMessage());
        firebaseData.put(BODY, "Đơn hàng " + order.getId() + " đã được tiếp nhận.");
        commonService.sendNotification(firebaseData, order.getCustomerId());
        return order;
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<OrderResponseDetail>> reject(String id) {
        Order order = sellerAction(id,
                Status.ACTIVE,
                OrderStatus.PENDING,
                ORDER_CANNOT_REJECT,
                OrderStatus.REJECTED);

        //refund for customer
        if (List.of(WALLET.name(), ZALO_PAY.name(), VN_PAY.name()).contains(order.getPaymentType())
                && order.getPaymentStatus().equals(PaymentStatus.PAID.name())) {
            Wallet wallet = walletService.customerRefund(order);
            transactionService.create(TransactionType.REFUND,
                    "Hoàn tiền cho do hàng bị từ chối", List.of(order.getId()), wallet, order.getAmount());
        }

        //push notification
        Map<String, String> firebaseData = new HashMap<>();
        firebaseData.put(TYPE, NotificationType.ORDER_REJECTED.name());
        firebaseData.put(TITLE, NotificationType.ORDER_REJECTED.getMessage());
        firebaseData.put(BODY, "Đơn hàng " + order.getId() + " đã bị từ chối");
        commonService.sendNotification(firebaseData, order.getCustomerId());

        return responseFactory.success("Bạn đã từ chối đơn hàng", buildResponseDetail(order));
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<OrderResponseDetail>> delivering(String id) {
        Order order = sellerAction(id,
                Status.ACTIVE,
                OrderStatus.ACCEPTED,
                ORDER_CANNOT_DELIVERING,
                OrderStatus.DELIVERING);
        entityManager.persist(order);

        //push notification
        Map<String, String> firebaseData = new HashMap<>();
        firebaseData.put(TYPE, NotificationType.ORDER_DELIVERY.name());
        firebaseData.put(TITLE, NotificationType.ORDER_DELIVERY.getMessage());
        firebaseData.put(BODY, "Đơn hàng " + order.getId() + " đang trên đường vận chuyển.");
        commonService.sendNotification(firebaseData, order.getCustomerId());

        return responseFactory.success("Đơn hàng đang trên đường vận chuyển.", buildResponseDetail(order));
    }

    @Override
    public ResponseEntity<BaseResponse<OrderResponseDetail>> done(String id) {
        String currentUserId = commonService.getCurrentUserId();

        Order order = orderRepository.findByIdAndStatusAndCustomerId(id, Status.ACTIVE.name(), currentUserId)
                .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, ORDER_NOT_FOUND));

        if (!order.getOrderStatus().equals(OrderStatus.DELIVERING.name())) {
            throw new APIException(HttpStatus.BAD_REQUEST, ORDER_CANNOT_RECEIVE);
        }

        order.setOrderStatus(OrderStatus.DONE.name());
        order = orderRepository.save(order);

        //pay for seller
        Wallet wallet = walletService.sellerPay(order);
        transactionService.create(TransactionType.SELL,
                "Thu tiền sản phẩm", List.of(order.getId()), wallet, order.getAmount());

        //push notification
        Map<String, String> firebaseData = new HashMap<>();
        firebaseData.put(TYPE, NotificationType.ORDER_DONE.name());
        firebaseData.put(TITLE, NotificationType.ORDER_DONE.getMessage());
        firebaseData.put(BODY, "Đơn hàng " + order.getId() + " đã được giao thành công.");
        commonService.sendNotification(firebaseData, order.getSellerId());

        return responseFactory.success("Đã nhận được hàng", buildResponseDetail(order));
    }

    @Transactional
    public Order sellerAction(String orderId, Status objectStatus,
                               OrderStatus currentOrderStatus, String throwMessage,
                               OrderStatus targetStatus) {
        String currentUserId = commonService.getCurrentUserId();

        Order order = orderRepository.findByIdAndStatusAndSellerId(orderId, objectStatus.name(), currentUserId)
                .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, ORDER_NOT_FOUND));

        if (!order.getOrderStatus().equals(currentOrderStatus.name())) {
            throw new APIException(HttpStatus.BAD_REQUEST, throwMessage);
        }

        order.setOrderStatus(targetStatus.name());
        entityManager.persist(order);
        return order;
    }

    private OrderResponseDetail buildResponseDetail(Order order) {
        OrderResponseDetail detail = orderMapper.entityToResponseDetail(order);
        detail.setCustomer(commonService.getUserInfo(order.getCustomerId()));
        detail.setSeller(commonService.getUserInfo(order.getSellerId()));
        detail.setProduct(commonService.getProductInfo(order.getProductId()));
        detail.setDestinationInfo(commonService.getDeliveryInfo(order.getDeliveryAddressId()));

        return detail;
    }

}