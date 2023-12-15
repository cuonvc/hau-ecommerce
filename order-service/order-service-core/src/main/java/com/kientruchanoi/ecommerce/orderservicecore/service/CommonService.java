package com.kientruchanoi.ecommerce.orderservicecore.service;

import com.kientruchanoi.ecommerce.authserviceshare.payload.response.DeliveryAddressResponse;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.orderservicecore.configuration.CustomUserDetail;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResponse;

public interface CommonService {

    CustomUserDetail getCurrentUser();
    String getCurrentUserId();

    DeliveryAddressResponse getDeliveryInfo(String id);

    UserResponse getUserInfo(String userId);
    ProductResponse getProductInfo(String productId);

    ProductResponse getProductInfoNotUser(String productId);
}
