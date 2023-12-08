package com.kientruchanoi.ecommerce.orderserviceshare.payload.request;

import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderRequest {

    @NotNull
    private String productId;

    @Size(min = 1, message = "Số lượng phải lớn hơn 0")
    private int quantity;

    @NotNull
    @NotBlank
    private String destinationAddress;

    private String note;

    @NotNull
    @NotBlank
    private PaymentType paymentType;


}
