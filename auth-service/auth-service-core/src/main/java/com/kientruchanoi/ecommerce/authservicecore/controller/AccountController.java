package com.kientruchanoi.ecommerce.authservicecore.controller;

import com.kientruchanoi.ecommerce.authservicecore.payload.request.PasswordChangeRequest;
import com.kientruchanoi.ecommerce.authservicecore.payload.request.RenewPasswordRequest;
import com.kientruchanoi.ecommerce.authservicecore.service.UserService;
import com.kientruchanoi.ecommerce.authserviceshare.payload.request.ProfileRequest;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.PageResponseUsers;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.baseservice.constant.PageConstant;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;

    @PostMapping("/password/forgot")
    public ResponseEntity<BaseResponse<String>> forgotPasswordRequest(@RequestBody String email) {
        return userService.forgotPasswordRequest(email);
    }

    @PostMapping("/password/renew")
    public ResponseEntity<BaseResponse<String>> renewPassword(@RequestBody RenewPasswordRequest request) {
        return userService.renewPassword(request);
    }

    @PutMapping("/account/edit")
    public ResponseEntity<BaseResponse<UserResponse>> editProfile(@Valid @RequestBody ProfileRequest request) {
        return userService.editProfile(request);
    }

    @PutMapping("/account/password")
    public ResponseEntity<BaseResponse<String>> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        return userService.changePassword(request);
    }

    @GetMapping("/account/{userId}")
    public ResponseEntity<BaseResponse<UserResponse>> getProfile(@PathVariable(name = "userId") String id) {
        return userService.getById(id);
    }

    @GetMapping("/moderator/account-list")
    public ResponseEntity<BaseResponse<PageResponseUsers>> getAllAccount(@RequestParam(value = "pageNo",
            defaultValue = PageConstant.PAGE_NO, required = false) Integer pageNo,
                                                                         @RequestParam(value = "pageSize",
                                                                                 defaultValue = PageConstant.PAGE_SIZE, required = false) Integer pageSize,
                                                                         @RequestParam(value = "sortBy",
                                                                                 defaultValue = PageConstant.SORT_BY, required = false) String sortBy,
                                                                         @RequestParam(value = "sortDir",
                                                                                 defaultValue = PageConstant.SORT_DIR, required = false) String sortDir) {
        return userService.getAll(pageNo, pageSize, sortBy, sortDir);
    }

    @PutMapping("/account/avatar")
    public ResponseEntity<BaseResponse<UserResponse>> uploadAvatar(@RequestPart(name = "image") MultipartFile file) {
        try {
            return userService.uploadAvatar(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/assign")
    public ResponseEntity<BaseResponse<String>> assign(@RequestParam("role") String role, @RequestParam("user_id") String userId) {
        return userService.assignRole(role.toUpperCase(), userId);
    }
}
