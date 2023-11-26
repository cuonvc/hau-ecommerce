package com.kientruchanoi.ecommerce.productservicecore.repository.custom;


import com.kientruchanoi.ecommerce.productservicecore.entity.ProductResource;

import java.util.List;

public interface ResourceCustomRepository {

    List<ProductResource> findByProductId(String id);
}
