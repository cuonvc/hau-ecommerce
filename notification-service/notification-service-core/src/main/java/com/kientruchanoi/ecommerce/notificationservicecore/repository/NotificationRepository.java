package com.kientruchanoi.ecommerce.notificationservicecore.repository;

import com.kientruchanoi.ecommerce.notificationservicecore.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    Optional<Notification> findByIdAndRecipient(String id, String userId);

    Page<Notification> findAllByRecipient(String id, Pageable pageable);

    List<Notification> findAllByRecipient(String id);

    @Query("UPDATE Notification n SET n.seen = false " +
            "WHERE n.recipient = :id")
    void updateAllSeenByUserId(String id);

}
