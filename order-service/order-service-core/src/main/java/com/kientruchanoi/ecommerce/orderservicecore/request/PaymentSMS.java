package com.kientruchanoi.ecommerce.orderservicecore.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSMS {
    private String uniqueId;
    private String slot;
    private String name;
    private String content;
}
