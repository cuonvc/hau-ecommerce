package com.kientruchanoi.ecommerce.orderserviceshare.payload.response;

import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResponse;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderResponseDetail {

    private String id;

    private ProductResponse product;

    private UserResponse customer;

    private UserResponse seller;

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
