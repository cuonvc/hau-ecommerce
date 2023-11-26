package com.kientruchanoi.ecommerce.authservicecore.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "refresh_token_clt")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RefreshToken {

    @Id
    private String id;

    @Field("token")
    private String token;

    @Field("expire_date")
    private Date expireDate;

    @Field("user_id")
    private String userId;
}
