package com.kientruchanoi.ecommerce.notificationserviceshare.payload.kafka;

import com.kientruchanoi.ecommerce.notificationserviceshare.enumerate.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NotificationBuilder implements Serializable {

    private String title;
    private String content;
    private String recipient;
    private String deviceToken;
    private Map<String, String> firebaseData;
    private NotificationType type;
    private Object resource;
//    private LocalDateTime builtAt;
}