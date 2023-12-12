package com.kientruchanoi.ecommerce.orderservicecore.entity;

import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.WalletStatus;
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

    @Field("balance_temporary")
    private Long balanceTemporary;

    @Field("total_amount_paid")
    private Long totalAmountPaid;

    @Field("sms_format")
    private String smsFormat;

    @Field("status")
    private String status = WalletStatus.DEPOSIT_PAID.name();

    @Field("created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Field("modified_date")
    private LocalDateTime modifiedDate = LocalDateTime.now();
}