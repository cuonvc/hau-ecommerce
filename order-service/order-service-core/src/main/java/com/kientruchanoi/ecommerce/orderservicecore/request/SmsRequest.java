package com.kientruchanoi.ecommerce.orderservicecore.request;

import lombok.Data;

@Data
public class SmsRequest {
    private String sender;
    private String content;
    private String time;
}
