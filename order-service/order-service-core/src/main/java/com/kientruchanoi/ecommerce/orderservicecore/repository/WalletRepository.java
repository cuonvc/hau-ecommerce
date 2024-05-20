package com.kientruchanoi.ecommerce.orderservicecore.repository;

import com.kientruchanoi.ecommerce.orderservicecore.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {

    Optional<Wallet> findByUserId(String userId);

    @Query("SELECT w FROM Wallet w " +
            "WHERE w.userId LIKE %:shortUserId")
    Optional<Wallet> findByShortUserId(String shortUserId);

    List<Wallet> findAllByStatus(String status);
}
