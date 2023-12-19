package com.kientruchanoi.ecommerce.orderserviceshare.payload.response;

import com.kientruchanoi.ecommerce.authserviceshare.payload.dto.BankAccount;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.WalletStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class WalletCustomResponse {
    private String id;

    private String userId;

    private String name;

    private BankAccount bankAccount;

    private Double balance;

    private Double balanceTemporary;

    private Double totalAmountPaid;

    private String smsFormat;

    private String status = WalletStatus.NORMAL.name();

    private LocalDateTime createdDate = LocalDateTime.now();

    private LocalDateTime modifiedDate = LocalDateTime.now();
}
