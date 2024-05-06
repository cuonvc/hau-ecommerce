package com.kientruchanoi.ecommerce.orderservicecore.repository;

import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    Optional<Order> findByIdAndStatus(String id, String status);

    List<Order> findAllBySellerIdAndOrderStatus(String sellerId, String status);

    List<Order> findAllByCustomerIdAndOrderStatus(String customerId, String status);

    List<Order> findAllBySellerId(String sellerId);

    List<Order> findAllByCustomerId(String customerId);

    Optional<Order> findByIdAndStatusAndCustomerId(String id, String status, String customerId);

    Optional<Order> findByIdAndStatusAndSellerId(String id, String status, String sellerId);

    @Query("SELECT o FROM Order o " +
            "JOIN OrderTransaction otr ON otr.orderId = o.id " +
            "WHERE otr.transactionId = :transactionId ")
    List<Order> findByTransactionId(String transactionId);
}
