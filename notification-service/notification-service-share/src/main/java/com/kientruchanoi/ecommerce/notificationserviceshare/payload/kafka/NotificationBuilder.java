package com.kientruchanoi.ecommerce.notificationserviceshare.payload.kafka;

import com.kientruchanoi.ecommerce.notificationserviceshare.enumerate.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NotificationBuilder {

    private String title;
    private String content;
    private List<String> recipients;
    private NotificationType type;
    private Object resource;
}
