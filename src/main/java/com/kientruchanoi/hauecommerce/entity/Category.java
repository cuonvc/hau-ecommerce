package com.kientruchanoi.hauecommerce.entity;

import com.kientruchanoi.hauecommerce.enumerate.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "category_clt")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    private String id;

    private String name;

    private String description;

    private String imageUrl;

    @Field("is_active")
    private Status isActive = Status.ACTIVE;
}
