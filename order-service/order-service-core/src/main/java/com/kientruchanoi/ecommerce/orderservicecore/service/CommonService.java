package com.kientruchanoi.ecommerce.orderservicecore.service;

import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.orderservicecore.configuration.CustomUserDetail;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResponse;

public interface CommonService {

    CustomUserDetail getCurrentUser();
    String getCurrentUserId();

    UserResponse getUserInfo(String userId);
    ProductResponse getProductInfo(String productId);
}
