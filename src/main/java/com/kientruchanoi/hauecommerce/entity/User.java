package com.kientruchanoi.hauecommerce.entity;

import com.kientruchanoi.hauecommerce.enumerate.Gender;
import com.kientruchanoi.hauecommerce.enumerate.Role;
import com.kientruchanoi.hauecommerce.enumerate.Status;
import lombok.AllArgsConstructor;
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
public class User {

    @Id
    private String id;

    @Field(name = "first_name")
    private String firstName;

    @Field(name = "last_name")
    private String lastName;

    @Field(name = "email")
    private String email;

    @Field(name = "password")
    private String password;

    @Field(name = "gender")
    private Gender gender = Gender.UNDEFINE;

    @Field(name = "avatar_url")
    private String avatarUrl;

    @Field(name = "about")
    private String about;

    @Field(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Field(name = "modified_date")
    private LocalDateTime modifiedDate = LocalDateTime.now();

    @Field(name = "status")
    private Status status = Status.ACTIVE;

    @Field(name = "role")
    private Role role = Role.USER;
}
