package com.kientruchanoi.ecommerce.authservicecore.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kientruchanoi.ecommerce.authserviceshare.payload.enumerate.Gender;
import com.kientruchanoi.ecommerce.authserviceshare.payload.enumerate.Role;
import com.kientruchanoi.ecommerce.authserviceshare.payload.enumerate.UserProvider;
import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "user_clt")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    @Id
    private String id;

    @Field(name = "first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("email")
    private String email;

    @Field("password")
    private String password;

    @Field(name = "gender")
    private String gender = Gender.UNDEFINE.toString();

    @Field("avatar_url")
    private String avatarUrl;

    @Field("cover_url")
    private String coverUrl;

    @Field("about")
    private String about;

    @Field("city")
    private String city;

    @Field("detail_address")
    private String detailAddress;

    @Field("created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Field("modified_date")
    private LocalDateTime modifiedDate = LocalDateTime.now();

    @Field("status")
    private String status = Status.ACTIVE.toString();

    @Field("user_provider")
    private String provider = UserProvider.SYSTEM.toString();

    @Field("role")
    private String role = Role.USER.toString();

    @Field("refresh_token")
    private RefreshToken refreshToken;
}
