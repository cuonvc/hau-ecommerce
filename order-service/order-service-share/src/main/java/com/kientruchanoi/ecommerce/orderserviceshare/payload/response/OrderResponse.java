package com.kientruchanoi.ecommerce.orderserviceshare.payload.response;

import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.OrderStatus;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.PaymentStatus;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.PaymentType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderResponse {

    private String id;

    private String productId;

    private String customerId;

    private String sellerId;

    private int quantity;

    private int amount;

    private String sourceAddress;

    private String destinationAddress;

    private String paymentType;

    private String paymentStatus;

    private String status;

    private String orderStatus;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;
}
