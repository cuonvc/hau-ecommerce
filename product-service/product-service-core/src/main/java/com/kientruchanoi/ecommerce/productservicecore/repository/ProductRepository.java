package com.kientruchanoi.ecommerce.productservicecore.repository;

import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import com.kientruchanoi.ecommerce.productservicecore.entity.Product;
import com.kientruchanoi.ecommerce.productservicecore.entity.ProductResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    Optional<Product> findByIdAndIsActive(String id, Status status);

    Optional<Product> findByIdAndUserId(String id, String userId);

    Integer countAllByUserId(String userId);

    Page<Product> findByIsActive(Pageable pageable, Status status);

    Page<Product> findAllByUserIdAndIsActive(Pageable pageable, String userId, Status status);

    Page<Product> findAllByIsActiveAndUserIdNotIn(Pageable pageable, Status status, String userId);

    List<Product> findByNameMatchesRegexOrBrandMatchesRegexAndIsActiveAndUserIdNotIn(
            String name, String brand, Status isActive, Collection<String> userIds);

    List<Product> findByNameRegexOrBrandRegexAndIsActive(String name, String brand, Status isActive);

    @Query("{$or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'brand': { $regex: ?0, $options: 'i' } } ], 'is_active': ?1 }")
    List<Product> findByNameOrBrand(String keyword, String status);

    @Query("{$or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'brand': { $regex: ?0, $options: 'i' } } ], 'is_active': ?1, 'user_id':  {$ne: ?2} }")
    List<Product> findByNameOrBrandNotOwner(String keyword, String status, String userId);

    Optional<Product> findByResources(ProductResource productResource);

//    @Query("UPDATE Product p SET p.isActive = :status WHERE p.commodity.id = :id")
//    @Modifying
//    void updateStatusByParentId(String id, Status status);
//
//    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.isActive = :status AND p.userId = :userId")
//    Optional<Product> findByIdAndOwner(String id, Status status, String userId);

}
