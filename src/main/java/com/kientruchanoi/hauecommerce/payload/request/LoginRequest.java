package com.kientruchanoi.hauecommerce.payload.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotNull
    @NotBlank(message = "Email không được để trống")
    @NotEmpty
    @Email
    private String email;

    @NotNull
    @NotBlank(message = "Password không được để trống")
    @NotEmpty
    @Size(min = 8, max = 20, message = "Password phải có từ 8 đến 12 ký tự")
    private String password;

}
