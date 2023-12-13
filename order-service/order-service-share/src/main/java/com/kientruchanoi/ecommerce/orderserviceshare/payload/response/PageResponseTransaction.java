package com.kientruchanoi.ecommerce.orderserviceshare.payload.response;

import com.kientruchanoi.ecommerce.baseservice.payload.response.PageResponse;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
public class PageResponseTransaction extends PageResponse<TransactionResponse> {
}
