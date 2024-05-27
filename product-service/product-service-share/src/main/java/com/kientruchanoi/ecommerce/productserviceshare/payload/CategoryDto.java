package com.kientruchanoi.ecommerce.productserviceshare.payload;

import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CategoryDto {

    private String id;

    @NotNull
    @NotBlank(message = "Tên không được để trống")
    @NotEmpty
    private String name;

    private String description;

    private String imageValue;

    private Status isActive;
}
