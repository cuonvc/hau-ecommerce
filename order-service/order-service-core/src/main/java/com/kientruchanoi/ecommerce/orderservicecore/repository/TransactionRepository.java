package com.kientruchanoi.ecommerce.orderservicecore.repository;

import com.kientruchanoi.ecommerce.orderservicecore.entity.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findAllByWalletId(String walletId);
}
