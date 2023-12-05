package com.kientruchanoi.ecommerce.authserviceshare.payload.response;

import com.kientruchanoi.ecommerce.authserviceshare.payload.dto.RefreshTokenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TokenObjectResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private RefreshTokenDto refreshToken;
    private UserResponse userResponse;
}