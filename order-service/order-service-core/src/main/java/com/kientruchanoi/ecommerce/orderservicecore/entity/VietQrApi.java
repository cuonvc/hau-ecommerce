package com.kientruchanoi.ecommerce.orderservicecore.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "viet_qr_api")
@Data
public class VietQrApi {

    @Id
    private String id;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "api_key")
    private String apiKey;
}
