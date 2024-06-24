package com.kientruchanoi.ecommerce.productservicecore.repository;

import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import com.kientruchanoi.ecommerce.productservicecore.entity.Product;
import com.kientruchanoi.ecommerce.productservicecore.entity.ProductResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    Optional<Product> findByIdAndIsActive(String id, String status);

    Optional<Product> findByIdAndUserId(String id, String userId);

    Integer countAllByUserId(String userId);

    Page<Product> findByIsActive(Pageable pageable, String status);

    Page<Product> findAllByUserId(Pageable pageable, String userId);

    Page<Product> findAllByUserIdAndIsActive(Pageable pageable, String userId, String status);

    @Query("SELECT p FROM Product p " +
            "WHERE p.isActive = :status " +
            "AND p.userId != :userId")
    Page<Product> findAllByIsActiveAndUserIdNotIn(Pageable pageable, String status, String userId);

//    List<Product> findByNameMatchesRegexOrBrandMatchesRegexAndIsActiveAndUserIdNotIn(
//            String name, String brand, Status isActive, Collection<String> userIds);
//
//    List<Product> findByNameRegexOrBrandRegexAndIsActive(String name, String brand, Status isActive);

    @Query("SELECT p FROM Product p " +
            "WHERE (" +
            "   LOWER(p.name) LIKE %:keyword% " +
            "   OR LOWER(p.brand) LIKE %:keyword% " +
            ") " +
            "AND p.isActive = :status")
    List<Product> findByNameOrBrand(String keyword, String status);

    @Query("SELECT p FROM Product p " +
            "WHERE (" +
            "   LOWER(p.name) LIKE %:keyword% " +
            "   OR LOWER(p.brand) LIKE %:keyword% " +
            ") " +
            "AND p.isActive = :status AND p.userId != :userId")
    List<Product> findByNameOrBrandNotOwner(String keyword, String status, String userId);

//    Optional<Product> findByResources(ProductResource productResource);

//    @Query("UPDATE Product p SET p.isActive = :status WHERE p.commodity.id = :id")
//    @Modifying
//    void updateStatusByParentId(String id, Status status);
//
//    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.isActive = :status AND p.userId = :userId")
//    Optional<Product> findByIdAndOwner(String id, Status status, String userId);

}
