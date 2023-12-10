package com.kientruchanoi.ecommerce.authservicecore.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Document(collection = "delivery_address_clt")
public class DeliveryAddress {

    @Id
    private String id;

    @Field("province")
    private String province;

    @Field("district")
    private String district;

    @Field("ward")
    private String ward;

    @Field("detail")
    private String detail;

    @Field("phone")
    private String phone;

    @Field("recipient_name")
    private String recipientName;

    @Field("user_id")
    private String userId;
}
