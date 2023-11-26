package com.kientruchanoi.ecommerce.productservicecore.service;

import com.kientruchanoi.ecommerce.productservicecore.entity.Product;
import com.kientruchanoi.ecommerce.productservicecore.entity.ProductResource;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResourceResponse;

import java.util.List;

public interface ProductResourceService {

    List<ProductResourceResponse> getImageUrls(String productId);

    List<ProductResource> initResources(Product product);

    List<ProductResource> getByProduct(Product product);

    void storeImagePath(String id, String field, String path);

//    void clearImagePath(String productId);
}
