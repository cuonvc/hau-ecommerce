package com.kientruchanoi.ecommerce.authservicecore.repository;

import com.kientruchanoi.ecommerce.authservicecore.entity.DeliveryAddress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryAddressRepository extends MongoRepository<DeliveryAddress, String> {

    Optional<DeliveryAddress> findByIdAndUserId(String id, String userId);
    List<DeliveryAddress> findByUserId(String userId);

    @Query("{'is_default': true, 'user_id': ?0}")
    DeliveryAddress findByDefaultIsAndUserId(String userId);
}
