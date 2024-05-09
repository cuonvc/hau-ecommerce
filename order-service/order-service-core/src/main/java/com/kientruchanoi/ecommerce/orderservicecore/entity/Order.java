package com.kientruchanoi.ecommerce.orderservicecore.entity;

import com.kientruchanoi.ecommerce.authserviceshare.payload.response.DeliveryAddressResponse;
import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.OrderStatus;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.PaymentStatus;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "order")
public class Order {

    @Id
    @GenericGenerator(name = "custom_id", strategy = "com.kientruchanoi.ecommerce.orderservicecore.util.CustomIdGenerator")
    @GeneratedValue(generator = "custom_id")
    private String id;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "seller_id")
    private String sellerId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "amount")
    private double amount;

    @Column(name = "wallet_id")
    private String walletId;

    @Transient
    private DeliveryAddressResponse sourceInfo;

    @Transient
    private DeliveryAddressResponse destinationInfo;

//    @Column(name = "source_address")
//    private String sourceAddress;
//
//    @Column(name = "destination_address")
//    private String destinationAddress;

    @Column(name = "note")
    private String note;

    @Column(name = "payment_type")
    private String paymentType = PaymentType.CASH.name();

    @Column(name = "payment_status")
    private String paymentStatus = PaymentStatus.UNPAID.name();

    @Column(name = "status")
    private String status = Status.ACTIVE.name();

    @Column(name = "order_status")
    private String orderStatus = OrderStatus.PENDING.name();

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate = LocalDateTime.now();
}
