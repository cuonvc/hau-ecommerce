package com.kientruchanoi.ecommerce.orderserviceshare.payload.response;

import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CartResponse {

    private String id;

    private UserResponse user;

    private List<ProductResponse> products;
}
