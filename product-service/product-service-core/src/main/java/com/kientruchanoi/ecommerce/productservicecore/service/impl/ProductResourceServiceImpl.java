package com.kientruchanoi.ecommerce.productservicecore.service.impl;

import com.kientruchanoi.ecommerce.baseservice.constant.ConstantVariable;
import com.kientruchanoi.ecommerce.productservicecore.entity.Category;
import com.kientruchanoi.ecommerce.productservicecore.entity.Product;
import com.kientruchanoi.ecommerce.productservicecore.entity.ProductResource;
import com.kientruchanoi.ecommerce.productservicecore.exception.ResourceNotFoundException;
import com.kientruchanoi.ecommerce.productservicecore.repository.CategoryRepository;
import com.kientruchanoi.ecommerce.productservicecore.repository.ProductRepository;
import com.kientruchanoi.ecommerce.productservicecore.repository.ProductResourceRepository;
import com.kientruchanoi.ecommerce.productservicecore.service.ProductResourceService;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResourceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductResourceServiceImpl implements ProductResourceService {

    private final ProductResourceRepository resourceRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;


    @Override
    public List<ProductResource> initResources(Product product) {
        List<ProductResource> resources = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            ProductResource resource = new ProductResource();
            resource.setProductId(product.getId());
            resources.add(resourceRepository.save(resource));
        }

        return resources;
    }

    @Override
    public List<ProductResource> getByProduct(Product product) {
        return resourceRepository.findByProductId(product.getId());
    }

    @Override
    public void storeImagePath(String id, String field, String path) {
        switch (field) {
            case "PRODUCT" -> {
                ProductResource resource = resourceRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Ảnh sản phẩm", "id", id)); //not happen

                resource.setImageUrl(path);
                resourceRepository.save(resource);
                updateResourceInProduct(resource);
            }

            case "CATEGORY" -> {
                Category category = categoryRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

                category.setImageUrl(path);
                categoryRepository.save(category);
            }

            default -> System.out.println("Not trigger");
        }

    }

    private void updateResourceInProduct(ProductResource resource) {
        Product product = productRepository.findById(resource.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Tài nguyên Sản phẩm", "id", resource.toString())); //not happen

        product.getResources().stream()
                .filter(r -> r.getId().equals(resource.getId()))
                .peek(r -> {
                    log.info("Triggerr 70 - {}", r);
                    r.setImageUrl(resource.getImageUrl());
                }).collect(Collectors.toSet());
        productRepository.save(product);
    }

//    @Override
//    @Transactional
//    public void clearImagePath(String productId) {
//        resourceRepository.deleteByProductId(productId);
//    }

    @Override
    public List<ProductResourceResponse> getImageUrls(String productId) {
        return resourceRepository.findByProductId(productId)
                .stream().map(element -> {
                    String path = element.getImageUrl();
                    if (path != null && path.contains("http")) {
                        return ProductResourceResponse.builder()
                                .id(element.getId())
                                .imageUrl(path)
                                .build();
                    }

                    return ProductResourceResponse.builder()
                            .id(element.getId())
                            .imageUrl(ConstantVariable.BASE_RESOURCE_DOMAIN + path)
                            .build();
                }).toList();
    }
}
