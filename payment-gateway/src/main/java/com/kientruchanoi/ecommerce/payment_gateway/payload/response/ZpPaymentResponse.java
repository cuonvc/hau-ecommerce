package com.kientruchanoi.ecommerce.payment_gateway.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ZpPaymentResponse {
    private String orderUrl;
    private String orderToken;
    private String returnMessage;
    private String subReturnMessage;
    private int subReturnCode;
    private String zpTransToken;
    private int returnCode;
}
