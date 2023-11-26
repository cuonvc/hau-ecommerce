package com.kientruchanoi.ecommerce.authserviceshare.payload.response;

import com.kientruchanoi.ecommerce.baseservice.payload.response.PageResponse;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
public class PageResponseUsers extends PageResponse<UserResponse> {

}
