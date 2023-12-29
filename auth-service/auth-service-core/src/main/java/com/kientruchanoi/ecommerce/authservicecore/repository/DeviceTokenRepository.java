package com.kientruchanoi.ecommerce.authservicecore.repository;

import com.kientruchanoi.ecommerce.authservicecore.entity.DeviceToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends MongoRepository<DeviceToken, String> {

    Optional<DeviceToken> findByUserId(String userId);
}
