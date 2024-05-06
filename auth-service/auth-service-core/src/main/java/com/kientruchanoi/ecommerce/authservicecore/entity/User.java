package com.kientruchanoi.ecommerce.authservicecore.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kientruchanoi.ecommerce.authserviceshare.payload.dto.BankAccount;
import com.kientruchanoi.ecommerce.authserviceshare.payload.enumerate.Gender;
import com.kientruchanoi.ecommerce.authserviceshare.payload.enumerate.Role;
import com.kientruchanoi.ecommerce.authserviceshare.payload.enumerate.UserProvider;
import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "user_tbl")
public class User {

    @Id
    @GenericGenerator(name = "custom_id", strategy = "com.kientruchanoi.ecommerce.authservicecore.util.CustomIdGenerator")
    @GeneratedValue(generator = "custom_id")
    private String id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "bank_account")
    private String bankAccountId;

    @Column(name = "gender")
    private String gender = Gender.UNDEFINE.toString();

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "about")
    private String about;

    @Column(name = "city")
    private String city;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate = LocalDateTime.now();

    @Column(name = "status")
    private String status = Status.ACTIVE.toString();

    @Column(name = "user_provider")
    private String provider = UserProvider.SYSTEM.toString();

    @Column(name = "role")
    private String role = Role.USER.toString();
}
