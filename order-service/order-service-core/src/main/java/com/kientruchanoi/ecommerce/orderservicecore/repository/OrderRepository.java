package com.kientruchanoi.ecommerce.orderservicecore.repository;

import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    Optional<Order> findByIdAndStatus(String id, String status);

    Optional<Order> findByCustomerIdOrSellerId(String userId);

    Optional<Order> findByIdAndStatusAndCustomerId(String id, String status, String customerId);

    Optional<Order> findByIdAndStatusAndSellerId(String id, String status, String sellerId);
}
