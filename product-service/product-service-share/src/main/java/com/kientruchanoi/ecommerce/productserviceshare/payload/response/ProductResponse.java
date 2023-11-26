package com.kientruchanoi.ecommerce.productserviceshare.payload.response;

import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.productserviceshare.payload.CategoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProductResponse {

    private String id;
    private String name;
    private Double standardPrice;
    private Double salePrice;
    private String code;
    private String description;
    private Integer remaining;
    private List<ProductResourceResponse> resources;
    private String brand;
    private Set<CategoryDto> categories;
    private UserResponse user;
}
