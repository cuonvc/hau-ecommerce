package com.kientruchanoi.ecommerce.authserviceshare.payload.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Email
    @NotNull
    @NotBlank(message = "Email không được để trống")
    @NotEmpty
    private String email;

    @NotEmpty
    @NotNull
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, max = 20, message = "Mật khẩu phải có từ 8 đến 20 ký tự")
    private String password;

    private String deviceToken;

}
