package com.kientruchanoi.ecommerce.productserviceshare.payload.response;

import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CategoryResponse {

    private String id;

    private String name;

    private String description;

    private String imageUrl;

    private Status isActive;
}
