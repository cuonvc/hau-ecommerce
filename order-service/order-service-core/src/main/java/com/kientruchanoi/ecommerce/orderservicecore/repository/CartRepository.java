package com.kientruchanoi.ecommerce.orderservicecore.repository;

import com.kientruchanoi.ecommerce.orderservicecore.entity.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {

    Optional<Cart> findByUserId(String userId);

    Optional<Cart> findByIdAndUserId(String id, String userId);
}
