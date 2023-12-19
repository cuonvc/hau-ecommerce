package com.kientruchanoi.ecommerce.authserviceshare.payload.request;

import com.kientruchanoi.ecommerce.authserviceshare.payload.dto.BankAccount;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RegRequest implements Serializable {

    @NotNull
    @NotBlank(message = "Họ không được để trống")
    @NotEmpty
    @Size(min = 1, max = 6, message = "Họ phải có từ 3 đến 6 ký tự")
    private String firstName;

    @NotNull
    @NotBlank(message = "Tên không được để trống")
    @NotEmpty
    @Size(min = 1, max = 6, message = "Tên phải có từ 3 đến 6 ký tự")
    private String lastName;

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

}
