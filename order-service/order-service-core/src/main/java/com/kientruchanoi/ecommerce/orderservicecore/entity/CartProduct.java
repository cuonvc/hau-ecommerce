package com.kientruchanoi.ecommerce.orderservicecore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "cart_product")
public class CartProduct {

    @Id
    @GenericGenerator(name = "custom_id", strategy = "com.kientruchanoi.ecommerce.orderservicecore.util.CustomIdGenerator")
    @GeneratedValue(generator = "custom_id")
    private String id;

    @Column(name = "cart_id")
    private String cartId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
