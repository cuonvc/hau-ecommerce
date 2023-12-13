package com.kientruchanoi.ecommerce.orderservicecore.entity;

import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.TransactionType;
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
@Document(collection = "transaction_clt")
public class Transaction {

    @Id
    private String id;

    @Field("wallet_id")
    private String walletId;

    @Field("amount")
    private long amount;

    @Field("balance")
    private long balance;

    @Field("type")
    private String type = TransactionType.INIT.name(); //default

    @Field("description")
    private String description;

    @Field("created_date")
    //khi người mua thanh toán, 2 transaction được tạo (type.SELL  cho người bán, type.BUY cho người mua)
    //cần phải set thời điểm tạo 2 tránsaction cùng lúc cho hai dòng lệnh (-> tạo biến local)
    private LocalDateTime createdDate;

}
