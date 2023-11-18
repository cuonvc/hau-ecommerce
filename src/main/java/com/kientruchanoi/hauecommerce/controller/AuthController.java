package com.kientruchanoi.hauecommerce.controller;

import com.kientruchanoi.hauecommerce.payload.repsonse.BaseResponse;
import com.kientruchanoi.hauecommerce.payload.repsonse.TokenObjectResponse;
import com.kientruchanoi.hauecommerce.payload.repsonse.UserResponse;
import com.kientruchanoi.hauecommerce.payload.request.LoginRequest;
import com.kientruchanoi.hauecommerce.payload.request.RegRequest;
import com.kientruchanoi.hauecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

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
