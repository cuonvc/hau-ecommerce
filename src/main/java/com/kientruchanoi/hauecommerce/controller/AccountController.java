package com.kientruchanoi.hauecommerce.controller;

import com.kientruchanoi.hauecommerce.payload.repsonse.BaseResponse;
import com.kientruchanoi.hauecommerce.payload.repsonse.PageResponseUsers;
import com.kientruchanoi.hauecommerce.payload.repsonse.UserResponse;
import com.kientruchanoi.hauecommerce.payload.request.PasswordChangeRequest;
import com.kientruchanoi.hauecommerce.payload.request.ProfileRequest;
import com.kientruchanoi.hauecommerce.payload.request.RenewPasswordRequest;
import com.kientruchanoi.hauecommerce.service.UserService;
import com.kientruchanoi.hauecommerce.utils.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;

    //OTP code sending to email
    @PostMapping("/password/forgot")
    public ResponseEntity<BaseResponse<String>> forgotPasswordRequest(@RequestBody String email) {
        return userService.forgotPasswordRequest(email);
    }

    //Type OTP code and password to change
    @PostMapping("/password/renew")
    public ResponseEntity<BaseResponse<String>> renewPassword(@RequestBody RenewPasswordRequest request) {
        return userService.renewPassword(request);
    }

    @PutMapping("/edit")
    public ResponseEntity<BaseResponse<UserResponse>> editProfile(@Valid @RequestBody ProfileRequest request) {
        return userService.editProfile(request);
    }

    //change password in account
    @PutMapping("/password/change")
    public ResponseEntity<BaseResponse<String>> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        return userService.changePassword(request);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<UserResponse>> getProfile(@PathVariable(name = "userId") String id) {
        return userService.getById(id);
    }

    @GetMapping("/moderator/account-list")
    public ResponseEntity<BaseResponse<PageResponseUsers>> getAllAccount(@RequestParam(value = "pageNo",
            defaultValue = Constants.PAGE_NO, required = false) Integer pageNo,
                                                                         @RequestParam(value = "pageSize",
                                                                                 defaultValue = Constants.PAGE_SIZE, required = false) Integer pageSize,
                                                                         @RequestParam(value = "sortBy",
                                                                                 defaultValue = Constants.SORT_BY, required = false) String sortBy,
                                                                         @RequestParam(value = "sortDir",
                                                                                 defaultValue = Constants.SORT_DIR, required = false) String sortDir) {
        return userService.getAll(pageNo, pageSize, sortBy, sortDir);
    }

    @PutMapping("/avatar")
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
