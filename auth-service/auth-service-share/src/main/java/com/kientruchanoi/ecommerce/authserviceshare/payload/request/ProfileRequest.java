package com.kientruchanoi.ecommerce.authserviceshare.payload.request;

import com.kientruchanoi.ecommerce.authserviceshare.payload.dto.BankAccount;
import com.kientruchanoi.ecommerce.authserviceshare.payload.enumerate.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProfileRequest {

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

    private String bankName;

    private String bankAccountBin;

    private String bankAccountNumber;

    private String bankAccountName;

//    @NotBlank
    private String gender;

    @Size(min = 1, max = 100, message = "Mô tả phải có từ 1 tới 100 ký tự")
    private String about;

    private String country;

    private String city;

    private String postalCode;

    private String detailAddress;
}
