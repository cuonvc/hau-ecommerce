package com.kientruchanoi.ecommerce.authserviceshare.payload.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

    @NotNull
    @NotBlank(message = "Mã ngân hàng không được để trống")
    @NotEmpty
    private String bin;

    @NotNull
    @NotBlank(message = "Tên ngân hàng không được để trống")
    @NotEmpty
    private String bankName;

    @NotNull
    @NotBlank(message = "Số tài khoản không được để trống")
    @NotEmpty
    private String number;

    @NotNull
    @NotBlank(message = "Tên tài khoản không được để trống")
    @NotEmpty
    private String name;
}
