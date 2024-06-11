package com.kientruchanoi.ecommerce.payment_gateway.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//defined by zalopay
public class ZaloPayCallbackRequest {
    private String data; //json
    private String mac;
    private int type;
}
