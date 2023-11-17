package com.kientruchanoi.hauecommerce.payload.repsonse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CategoryResponse {

    private String id;

    private String name;

    private String description;

    private String imageUrl;

}
