package com.kientruchanoi.ecommerce.orderservicecore.service;

import com.kientruchanoi.ecommerce.authserviceshare.payload.response.DeliveryAddressResponse;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.notificationserviceshare.enumerate.NotificationType;
import com.kientruchanoi.ecommerce.orderservicecore.configuration.CustomUserDetail;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResponse;

import java.util.List;
import java.util.Map;

public interface CommonService {

    CustomUserDetail getCurrentUser();
    String getCurrentUserId();

    DeliveryAddressResponse getDeliveryInfo(String id);

    UserResponse getUserInfo(String userId);

    String getDeviceToken(String userId);

    List<String> getAllAdminId();

    ProductResponse getProductInfo(String productId);

    ProductResponse getProductInfoNotUser(String productId);

    void sendNotification(Map<String, String> firebaseData, String recipient);
}