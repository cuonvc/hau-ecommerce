package com.kientruchanoi.ecommerce.orderservicecore.repository;

import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    Optional<Order> findByIdAndStatus(String id, String status);

    Optional<Order> findByCustomerIdOrSellerId(String userId);

    List<Order> findAllByStatusAndCustomerIdOrSellerId(String status, String customerId, String sellerId);

    @Query("{'order_status': ?0, $or: [{'customer_id': ?1}, {'seller_id': ?1}]}")
    List<Order> findAllByUserIdAndOrderStatus(String status, String userId);

    List<Order> findAllByCustomerIdOrSellerId(String customerId, String sellerId);

    Optional<Order> findByIdAndStatusAndCustomerId(String id, String status, String customerId);

    Optional<Order> findByIdAndStatusAndSellerId(String id, String status, String sellerId);
}
