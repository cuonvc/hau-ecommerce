package com.kientruchanoi.ecommerce.authserviceshare.payload.response;

import ch.qos.logback.core.status.Status;
import com.kientruchanoi.ecommerce.authserviceshare.payload.enumerate.Gender;
import com.kientruchanoi.ecommerce.authserviceshare.payload.enumerate.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String avatarUrl;
    private String about;
    private String city;
    private String detailAddress;
    private String role;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String status;
}
