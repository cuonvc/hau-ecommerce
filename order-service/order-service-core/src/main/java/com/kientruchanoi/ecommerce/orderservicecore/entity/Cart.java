package com.kientruchanoi.ecommerce.orderservicecore.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "cart_clt")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Cart {

    @Id
    private String id;

    @Field("user_Id")
    private String userId;

    @Field("product_ids")
    private List<String> productIds = new ArrayList<>();
}
