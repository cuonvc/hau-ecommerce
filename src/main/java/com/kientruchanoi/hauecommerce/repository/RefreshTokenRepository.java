package com.kientruchanoi.hauecommerce.repository;

import com.kientruchanoi.hauecommerce.entity.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> getByUserId(String userId);

    Optional<RefreshToken> getByToken(String token);
}
