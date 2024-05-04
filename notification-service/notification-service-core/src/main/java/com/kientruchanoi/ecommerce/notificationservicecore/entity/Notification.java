package com.kientruchanoi.ecommerce.notificationservicecore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "type")
    private String type;  //enum name

    @Column(name = "recipient")
    private String recipient;

    @Column(name = "seen")
    private boolean seen = false;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
}
