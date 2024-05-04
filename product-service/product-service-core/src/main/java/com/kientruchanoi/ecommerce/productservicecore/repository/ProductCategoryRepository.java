package com.kientruchanoi.ecommerce.productservicecore.repository;

import com.kientruchanoi.ecommerce.productservicecore.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, String> {
}
