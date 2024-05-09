package com.kientruchanoi.ecommerce.orderservicecore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GenericGenerator(name = "custom_id", strategy = "com.kientruchanoi.ecommerce.orderservicecore.util.CustomIdGenerator")
    @GeneratedValue(generator = "custom_id")
    private String id;

    @Column(name = "user_Id")
    private String userId;
}
