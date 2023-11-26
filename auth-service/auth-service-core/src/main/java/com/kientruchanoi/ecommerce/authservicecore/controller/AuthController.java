package com.kientruchanoi.ecommerce.authservicecore.controller;

//import com.lender.authservice.payload.response.BaseResponse;

import com.kientruchanoi.ecommerce.authservicecore.service.TokenService;
import com.kientruchanoi.ecommerce.authservicecore.service.UserService;
import com.kientruchanoi.ecommerce.authserviceshare.payload.request.LoginRequest;
import com.kientruchanoi.ecommerce.authserviceshare.payload.request.RegRequest;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.TokenObjectResponse;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final TokenService tokenService;

    @GetMapping("/gateway/valid")  //API valid token for gateway service
    public boolean validateToken(@RequestParam("token") String token) {
        return tokenService.validateAccessToken(token);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<BaseResponse<String>> signup(@Valid @RequestBody RegRequest request) {
        return userService.register(request);
    }

    @PostMapping("/active-account")
    //client caching RegRequest to browser when user click sign up
    public ResponseEntity<BaseResponse<UserResponse>> active(@RequestBody RegRequest request, @RequestParam("active_key") String key) {
        return userService.validate(request, key);
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<TokenObjectResponse>> login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<String>> logout() {
        return userService.logout();
    }

    @GetMapping("/token/renew")
    public ResponseEntity<BaseResponse<TokenObjectResponse>> renewAccessToken(@RequestParam("refresh_token") String refreshToken) {
        return userService.renewAccessToken(refreshToken);
    }

}
