package com.kientruchanoi.ecommerce.authservicecore.repository;

import com.kientruchanoi.ecommerce.authservicecore.entity.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByUserId(String userId);

    Optional<RefreshToken> findByToken(String token);
}
