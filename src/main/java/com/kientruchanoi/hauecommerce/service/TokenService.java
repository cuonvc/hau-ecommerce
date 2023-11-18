package com.kientruchanoi.hauecommerce.service;

import com.kientruchanoi.hauecommerce.entity.RefreshToken;
import com.kientruchanoi.hauecommerce.entity.User;
import org.springframework.stereotype.Service;

public interface TokenService {

    boolean validateAccessToken(String token);

    void initRefreshToken(User user);

    void clearToken(String userId);

    RefreshToken generateTokenObject(User user);
}
