package com.kientruchanoi.ecommerce.orderserviceshare.payload.request;

import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderRequest {

    @NotNull
    private String cartId;

    @NotNull
    private List<String> productIds;

    @NotNull
    private String deliveryDestinationId;

    private String note;

    @NotNull
    @NotBlank
    private PaymentType paymentType;


}
