package com.kientruchanoi.ecommerce.orderservicecore.entity;

import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.WalletStatus;
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
@Table(name = "wallet")
public class Wallet {

    @Id
    @GenericGenerator(name = "custom_id", strategy = "com.kientruchanoi.ecommerce.orderservicecore.util.CustomIdGenerator")
    @GeneratedValue(generator = "custom_id")
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "balance")
    private Double balance;

    @Column(name = "balance_temporary")
    private Double balanceTemporary;

    @Column(name = "total_amount_paid")
    private Double totalAmountPaid;

    @Column(name = "sms_format")
    private String smsFormat;

    @Column(name = "status")
    private String status = WalletStatus.NORMAL.name();

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate = LocalDateTime.now();
}
