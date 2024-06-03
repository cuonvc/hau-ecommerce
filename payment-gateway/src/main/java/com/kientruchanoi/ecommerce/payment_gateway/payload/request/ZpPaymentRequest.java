package com.kientruchanoi.ecommerce.payment_gateway.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ZpPaymentRequest {

    @NotNull
    private ItemRequest[] itemRequest;

    @NotNull
    private int amount;
}
