package com.kientruchanoi.hauecommerce.repository;

import com.kientruchanoi.hauecommerce.entity.Category;
import com.kientruchanoi.hauecommerce.enumerate.Status;
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
}
