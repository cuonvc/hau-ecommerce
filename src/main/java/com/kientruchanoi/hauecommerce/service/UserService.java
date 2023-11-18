package com.kientruchanoi.hauecommerce.service;

import com.kientruchanoi.hauecommerce.payload.repsonse.BaseResponse;
import com.kientruchanoi.hauecommerce.payload.repsonse.PageResponseUsers;
import com.kientruchanoi.hauecommerce.payload.repsonse.TokenObjectResponse;
import com.kientruchanoi.hauecommerce.payload.repsonse.UserResponse;
import com.kientruchanoi.hauecommerce.payload.request.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

    ResponseEntity<BaseResponse<String>> register(RegRequest request);

    ResponseEntity<BaseResponse<UserResponse>> validate(RegRequest request, String key);

    ResponseEntity<BaseResponse<TokenObjectResponse>> login(LoginRequest request);

    ResponseEntity<BaseResponse<String>> logout();

    ResponseEntity<BaseResponse<TokenObjectResponse>> renewAccessToken(String refreshToken);

    ResponseEntity<BaseResponse<String>> forgotPasswordRequest(String email);

    ResponseEntity<BaseResponse<String>> renewPassword(RenewPasswordRequest request);

    ResponseEntity<BaseResponse<UserResponse>> editProfile(ProfileRequest request);

    ResponseEntity<BaseResponse<String>> changePassword(PasswordChangeRequest request);

    ResponseEntity<BaseResponse<UserResponse>> getById(String id);

    ResponseEntity<BaseResponse<PageResponseUsers>> getAll(Integer pageNo, Integer pageSize, String sortBy, String sortDir);

    ResponseEntity<BaseResponse<UserResponse>> uploadAvatar(MultipartFile file) throws IOException;

//    void saveChangeImage(String userId, String field, String path);

    ResponseEntity<BaseResponse<String>> assignRole(String role, String userId);
}
