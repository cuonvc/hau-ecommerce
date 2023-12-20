package com.kientruchanoi.ecommerce.authservicecore.service;

import com.kientruchanoi.ecommerce.authservicecore.config.CustomUserDetail;

public interface CommonService {

    CustomUserDetail getCurrentUser();

    String getCurrentUserId();

    Integer getProductsByOwner();

    Integer getProductsByUser(String userId);
}
