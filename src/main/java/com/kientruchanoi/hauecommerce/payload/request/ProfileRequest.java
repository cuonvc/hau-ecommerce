package com.kientruchanoi.hauecommerce.payload.request;

import com.kientruchanoi.hauecommerce.enumerate.Gender;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "First name không được để trống")
    @Size(min = 3, max = 6, message = "First name phải có từ 3 đến 6 ký tự")
    private String firstName;

    @NotBlank(message = "Last name không được để trống")
    @Size(min = 3, max = 6, message = "Last name phải có từ 3 đến 6 ký tự")
    private String lastName;

//    @NotBlank
    private Gender gender;

    @Size(min = 1, max = 100, message = "Từ 1 tới 100 ký tụ")
    private String about;
}
