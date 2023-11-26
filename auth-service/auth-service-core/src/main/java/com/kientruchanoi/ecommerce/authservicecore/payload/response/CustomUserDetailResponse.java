package com.kientruchanoi.ecommerce.authservicecore.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CustomUserDetailResponse {
    private String id;
    private String email;
    private List<String> grantedAuthorities;
}
