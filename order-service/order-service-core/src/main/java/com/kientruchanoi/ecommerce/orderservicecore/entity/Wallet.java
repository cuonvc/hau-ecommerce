package com.kientruchanoi.ecommerce.orderservicecore.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Document(collection = "wallet_clt")
public class Wallet {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("balance")
    private Long balance;

    @Field("total_amount_paid")
    private Long totalAmountPaid;

    @Field("created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Field("modified_date")
    private LocalDateTime modifiedDate = LocalDateTime.now();
}
