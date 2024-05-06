package com.kientruchanoi.ecommerce.productservicecore.entity;

import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GenericGenerator(name = "custom_id", strategy = "com.kientruchanoi.ecommerce.productservicecore.util.CustomIdGenerator")
    @GeneratedValue(generator = "custom_id")
    private String id;

    private String name;

    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_active")
    private String isActive = Status.ACTIVE.name();

}
