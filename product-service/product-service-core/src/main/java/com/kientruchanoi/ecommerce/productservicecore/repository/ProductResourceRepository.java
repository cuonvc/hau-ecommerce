package com.kientruchanoi.ecommerce.productservicecore.repository;

import com.kientruchanoi.ecommerce.productservicecore.entity.ProductResource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductResourceRepository extends MongoRepository<ProductResource, String> {

//    @Query("SELECT r FROM ProductResource r WHERE r.product.id = :productId")
//    List<ProductResource> findByProductId(String productId);
//
//    @Modifying
//    @Query("DELETE FROM ProductResource r WHERE r.product.id = :productId")
//    void deleteByProductId(String productId);

    List<ProductResource> findByProductId(String id);
}
