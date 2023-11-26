package com.kientruchanoi.ecommerce.productservicecore.repository;

import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import com.kientruchanoi.ecommerce.productservicecore.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    Optional<Category> findByNameAndIsActive(String name, Status status);

    Page<Category> findAllByIsActive(Pageable pageable, Status status);

    Optional<Category> findByIdAndIsActive(String id, Status status);

//    @Query("SELECT c FROM Category c WHERE c.name = :name")
//    Optional<Category> findByName(String name);
//
//    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.isActive = :status")
//    Optional<Category> findByIdAndStatus(String id, Status status);
//
//    @Query("SELECT c FROM Category c WHERE c.parentId = :parentId AND c.isActive = :status")
//    Set<Category> findByParentIdAndStatus(String parentId, Status status);
//
//    @Query("SELECT c FROM Category c WHERE c.parentId = null")
//    Page<Category> findAllByRoot(Pageable pageable);
}
