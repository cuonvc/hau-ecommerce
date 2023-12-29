package com.kientruchanoi.ecommerce.notificationservicecore.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PushNotification {

    private String to;  //device token
    private String sound;
    private String title;
    private String body;
    private Object data;
}
