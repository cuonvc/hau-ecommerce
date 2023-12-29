package com.kientruchanoi.ecommerce.notificationservicecore.repository;

import com.kientruchanoi.ecommerce.notificationservicecore.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    Optional<Notification> findByIdAndRecipient(String id, String userId);

    Page<Notification> findAllByRecipient(String id, Pageable pageable);

    List<Notification> findAllByRecipient(String id);

    @Query("{'user_id': ?0, 'seen': false}")
    void updateAllSeenByUserId(String id);

}
