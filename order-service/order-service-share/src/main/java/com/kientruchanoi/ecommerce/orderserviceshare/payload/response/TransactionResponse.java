package com.kientruchanoi.ecommerce.orderserviceshare.payload.response;

import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TransactionResponse {

    private String id;

    private String walletId;

    private long amount;

    private String type;

    private String description;

    private LocalDateTime createdDate;
}
