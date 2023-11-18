package com.kientruchanoi.hauecommerce.payload.repsonse;

import com.kientruchanoi.hauecommerce.enumerate.Gender;
import com.kientruchanoi.hauecommerce.enumerate.Role;
import com.kientruchanoi.hauecommerce.enumerate.Status;
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
    private Gender gender;
    private String avatarUrl;
    private String about;
    private Role role;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
