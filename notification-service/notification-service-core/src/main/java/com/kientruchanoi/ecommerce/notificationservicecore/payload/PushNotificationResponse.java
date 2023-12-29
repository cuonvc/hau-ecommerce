package com.kientruchanoi.ecommerce.notificationservicecore.payload;

import lombok.Data;

@Data
public class PushNotificationResponse {
    private Data data;

    @lombok.Data
    class Data {
        public String status;
        public String id;
    }
}
