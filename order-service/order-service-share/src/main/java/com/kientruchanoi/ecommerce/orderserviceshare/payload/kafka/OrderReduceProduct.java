package com.kientruchanoi.ecommerce.orderserviceshare.payload.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderReduceProduct {

    private String productId;
    private String reduceQuantity;
}
