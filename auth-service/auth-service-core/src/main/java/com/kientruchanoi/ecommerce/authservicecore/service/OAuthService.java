package com.kientruchanoi.ecommerce.authservicecore.service;

import com.kientruchanoi.ecommerce.authserviceshare.payload.response.TokenObjectResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import org.springframework.http.ResponseEntity;

public interface OAuthService {
//    ResponseEntity<BaseResponse<TokenObjectResponse>> validateToken(String provider, String token);

    ResponseEntity<BaseResponse<TokenObjectResponse>> validateGoogleToken(String token);
    ResponseEntity<BaseResponse<TokenObjectResponse>> validateGithubCode(String code);

    ResponseEntity<BaseResponse<TokenObjectResponse>> validateFacebookToken(String token);
}
