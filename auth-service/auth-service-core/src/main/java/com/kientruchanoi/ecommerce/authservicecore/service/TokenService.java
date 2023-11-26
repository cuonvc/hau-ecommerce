package com.kientruchanoi.ecommerce.authservicecore.service;


import com.kientruchanoi.ecommerce.authservicecore.entity.RefreshToken;
import com.kientruchanoi.ecommerce.authservicecore.entity.User;

public interface TokenService {

    boolean validateAccessToken(String token);

    void initRefreshToken(User user);

    void clearToken(String userId);

    RefreshToken generateTokenObject(User user);
}
