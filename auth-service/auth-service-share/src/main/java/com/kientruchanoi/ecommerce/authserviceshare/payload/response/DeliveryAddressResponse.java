package com.kientruchanoi.ecommerce.authserviceshare.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DeliveryAddressResponse {

    private String id;

    private String province;

    private String district;

    private String ward;

    private String detail;

    private String phone;

    private String recipientName;

    private String userId;
}
