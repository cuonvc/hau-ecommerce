package com.kientruchanoi.ecommerce.authservicecore.repository;

import com.kientruchanoi.ecommerce.authservicecore.entity.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, String> {

    Optional<DeliveryAddress> findByIdAndUserId(String id, String userId);
    List<DeliveryAddress> findByUserId(String userId);

    @Query("SELECT da FROM DeliveryAddress da " +
            "WHERE da.isDefault = true AND da.userId = :userId")
    Optional<DeliveryAddress> findByDefaultIsAndUserId(String userId);

    Integer countByUserId(String userId);
}
