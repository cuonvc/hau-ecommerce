package com.kientruchanoi.ecommerce.payment_gateway.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ZaloGetListBankResponse {
    private Integer maxAmount;
    private String name;
    private Integer displayOrder;
    private Integer pmcid;
    private Integer minAmount;
    private String bankCode;
}
