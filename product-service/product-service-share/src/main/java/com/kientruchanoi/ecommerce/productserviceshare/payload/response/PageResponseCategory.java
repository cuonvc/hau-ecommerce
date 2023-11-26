package com.kientruchanoi.ecommerce.productserviceshare.payload.response;

import com.kientruchanoi.ecommerce.baseservice.payload.response.PageResponse;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
public class PageResponseCategory extends PageResponse<CategoryResponse> {

}
