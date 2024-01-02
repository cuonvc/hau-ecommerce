package com.kientruchanoi.ecommerce.orderservicecore.repository;

import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    Optional<Order> findByIdAndStatus(String id, String status);

    Optional<Order> findByCustomerIdOrSellerId(String userId);

    List<Order> findAllByStatusAndCustomerIdOrSellerId(String status, String customerId, String sellerId);

    Page<Order> findAllBySellerIdAndOrderStatus(String sellerId, String status, Pageable pageable);

    @Query("{'seller_id': ?0, 'order_status': ?1, 'created_date': {$gte: ?2, $lte: ?3}}")
    Page<Order> findAllBySellerIdAndOrderStatusAndRange(String sellerId, String orderStatus, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<Order> findAllByCustomerIdAndOrderStatus(String customerId, String orderStatus, Pageable pageable);

    @Query("{'customer_id': ?0, 'order_status': ?1, 'created_date': {$gte: ?2, $lte: ?3}}")
    Page<Order> findAllByCustomerIdAndOrderStatusAndRange(String customerId, String orderStatus, LocalDateTime from, LocalDateTime to, Pageable pageable);

    @Query("{'created_date': {$gte: ?2, $lte: ?3}}")
    Page<Order> findAllByRange(LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<Order> findAllByOrderStatus(String orderStatus, Pageable pageable);

    @Query("{'order_status': ?0, 'status': ?1, 'created_date': {$gte: ?2, $lte: ?3}}")
    Page<Order> findAllByOrderStatusAndRange(String orderStatus, LocalDateTime from, LocalDateTime to, Pageable pageable);

    List<Order> findAllByCustomerIdOrSellerId(String customerId, String sellerId);

    Page<Order> findAllBySellerId(String sellerId, Pageable pageable);

    @Query("{'seller_id': ?0, 'created_date': {$gte: ?1, $lte: ?2}}")
    Page<Order> findAllBySellerIdAndRange(String sellerId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<Order> findAllByCustomerId(String customerId, Pageable pageable);

    @Query("{'customer_id': ?0, 'created_date': {$gte: ?1, $lte: ?2}}")
    Page<Order> findAllByCustomerIdAndRange(String customerId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Optional<Order> findByIdAndStatusAndCustomerId(String id, String status, String customerId);

    Optional<Order> findByIdAndStatusAndSellerId(String id, String status, String sellerId);
}
