package com.kientruchanoi.ecommerce.authservicecore.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "device_token_clt")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DeviceToken {

    @Id
    private String id;

    @Field("token")
    private String token;

    @Field("user_id")
    private String userId;
}
