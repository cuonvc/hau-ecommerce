package com.kientruchanoi.hauecommerce.payload.request;

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

    @NotBlank(message = "First name không được để trống")
    @Size(min = 3, max = 6, message = "First name phải có từ 3 đến 6 ký tự")
    private String firstName;

    @NotBlank(message = "Last name không được để trống")
    @Size(min = 3, max = 6, message = "Last name phải có từ 3 đến 6 ký tự")
    private String lastName;

    @Email
    @NotNull
    @NotBlank(message = "Email không được để trống")
    @NotEmpty
    private String email;

    @NotNull
    @NotBlank(message = "Password không được để trống")
    @NotEmpty
    @Size(min = 8, max = 20, message = "Password phải có từ 8 đến 12 ký tự")
    private String password;

}
