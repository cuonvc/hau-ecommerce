package com.kientruchanoi.ecommerce.orderservicecore.entity;

import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GenericGenerator(name = "custom_id", strategy = "com.kientruchanoi.ecommerce.orderervicecore.util.CustomIdGenerator")
    @GeneratedValue(generator = "custom_id")
    private String id;

    @Column(name = "wallet_id")
    private String walletId;  //using for withdraw, deposit

    @Column(name = "amount")
    private double amount;

    @Column(name = "balance")
    private double balance;

    @Column(name = "type")
    private String type = TransactionType.INIT.name(); //default

    @Column(name = "description")
    private String description;

    @Column(name = "created_date")
    //khi người mua thanh toán, 2 transaction được tạo (type.SELL  cho người bán, type.BUY cho người mua)
    //cần phải set thời điểm tạo 2 tránsaction cùng lúc cho hai dòng lệnh (-> tạo biến local)
    private LocalDateTime createdDate = LocalDateTime.now();

}
