package com.kientruchanoi.ecommerce.authservicecore.service;

import com.kientruchanoi.ecommerce.authservicecore.config.CustomUserDetail;

import java.util.Map;

public interface CommonService {

    CustomUserDetail getCurrentUser();

    String getCurrentUserId();

    Integer getProductsByOwner();

    Integer getProductsByUser(String userId);

    String getDeviceToken(String userId);

    void sendNotification(Map<String, String> firebaseData, String recipient);
}
