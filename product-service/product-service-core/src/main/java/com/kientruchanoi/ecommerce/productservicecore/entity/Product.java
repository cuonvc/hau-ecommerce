package com.kientruchanoi.ecommerce.productservicecore.entity;

import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GenericGenerator(name = "custom_id", strategy = "com.kientruchanoi.ecommerce.productservicecore.util.CustomIdGenerator")
    @GeneratedValue(generator = "custom_id")
    private String id;

    private String code;

    private String name;

    private String description;

    private String brand;

    @Column(name = "standard_price")
    private Double standardPrice;

    @Column(name = "sale_price")
    private Double salePrice;

    @Column(name = "sale_expire_at")
    private LocalDateTime saleExpireAt;

    @Column(name = "remaining")
    private Integer remaining = 0;

    @Column(name = "sold")
    private Integer sold = 0;

    @Column(name = "is_active")
    private String isActive = Status.ACTIVE.name();

    @Column(name = "user_id")
    private String userId;

//    @Column(name = "category_id")
//    private String categoryId;

}
