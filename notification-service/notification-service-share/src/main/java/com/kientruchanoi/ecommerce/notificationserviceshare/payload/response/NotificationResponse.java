package com.kientruchanoi.ecommerce.notificationserviceshare.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NotificationResponse {

    private String id;
    private String title;
    private String content;
    private List<String> userIds;
    private boolean seen;
    private LocalDateTime createdDate;
}
