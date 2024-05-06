package com.kientruchanoi.ecommerce.productservicecore.service.impl;

import com.kientruchanoi.ecommerce.baseservice.constant.ConstantVariable;
import com.kientruchanoi.ecommerce.productservicecore.entity.Category;
import com.kientruchanoi.ecommerce.productservicecore.entity.Product;
import com.kientruchanoi.ecommerce.productservicecore.entity.ProductResource;
import com.kientruchanoi.ecommerce.productservicecore.exception.ResourceNotFoundException;
import com.kientruchanoi.ecommerce.productservicecore.repository.CategoryRepository;
import com.kientruchanoi.ecommerce.productservicecore.repository.ProductRepository;
import com.kientruchanoi.ecommerce.productservicecore.repository.ProductResourceRepository;
import com.kientruchanoi.ecommerce.productservicecore.service.FileImageService;
import com.kientruchanoi.ecommerce.productservicecore.service.ProductResourceService;
import com.kientruchanoi.ecommerce.productserviceshare.payload.request.ProductResourceRequest;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResourceResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Base64;
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
    private final FileImageService fileImageService;
    private final EntityManager entityManager;


    @Override
    @Transactional
    public List<ProductResource> initResources(Product product, List<ProductResourceRequest> request) {
        List<ProductResource> resources = new ArrayList<>();
        for (ProductResourceRequest resourceRequest : request) {
            ProductResource resource = new ProductResource();
            resource.setProductId(product.getId());
            resource.setImageUrl(fileImageService
                    .saveImageFile(Base64.getDecoder()
                            .decode(resourceRequest.getImageValue())));
            entityManager.persist(resource);
            resources.add(resource);
        }

        return resources;
    }

    @Override
    public List<ProductResource> updateResources(Product product, List<ProductResourceRequest> resourceRequests) {
        resourceRepository.deleteAllByProductId(product.getId());
        return initResources(product, resourceRequests);
    }

//    @Override
//    public List<ProductResource> getByProduct(Product product) {
//        return resourceRepository.findByProductId(product.getId());
//    }
//
//    @Override
//    public void storeImagePath(String id, String field, String path) {
//        switch (field) {
//            case "PRODUCT" -> {
//                ProductResource resource = resourceRepository.findById(id)
//                        .orElseThrow(() -> new ResourceNotFoundException("Ảnh sản phẩm", "id", id)); //not happen
//
//                resource.setImageUrl(path);
//                resourceRepository.save(resource);
//                updateResourceInProduct(resource);
//            }
//
//            case "CATEGORY" -> {
//                Category category = categoryRepository.findById(id)
//                        .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
//
//                category.setImageUrl(path);
//                categoryRepository.save(category);
//            }
//
//            default -> System.out.println("Not trigger");
//        }
//
//    }

    @Override
    public List<ProductResourceResponse> getImageUrls(String productId) {
        return resourceRepository.findByProductId(productId)
                .stream().map(element -> ProductResourceResponse.builder()
                        .id(element.getId())
                        .imageUrl(element.getImageUrl())
                        .build()).toList();
    }
}
