package com.kientruchanoi.ecommerce.productservicecore.entity;

import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("category_clt")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class Category {

    @Id
    private String id;

    private String name;

    private String description;

    @Field("image_url")
    private String imageUrl;

    @Field("is_active")
    private Status isActive = Status.ACTIVE;

}
