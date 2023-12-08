package com.kientruchanoi.ecommerce.orderservicecore.entity;

import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.OrderStatus;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.PaymentStatus;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "order_clt")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Order {

    @Id
    private String id;

    @Field("product_id")
    private String productId;

    @Field("customer_id")
    private String customerId;

    @Field("seller_id")
    private String sellerId;

    @Field("quantity")
    private int quantity;

    @Field("amount")
    private double amount;

    @Field("source_address")
    private String sourceAddress;

    @Field("destination_address")
    private String destinationAddress;

    @Field("note")
    private String note;

    @Field("payment_type")
    private String paymentType = PaymentType.CASH.name();

    @Field("payment_status")
    private String paymentStatus = PaymentStatus.UNPAID.name();

    @Field("status")
    private String status = Status.ACTIVE.name();

    @Field("order_status")
    private String orderStatus = OrderStatus.PENDING.name();

    @Field("created_date")
    private LocalDateTime createdDate;

    @Field("modified_date")
    private LocalDateTime modifiedDate;
}
