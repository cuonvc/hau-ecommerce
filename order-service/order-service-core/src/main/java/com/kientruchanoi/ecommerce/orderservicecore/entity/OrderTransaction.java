package com.kientruchanoi.ecommerce.orderservicecore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "order_transaction") //môỗi giỏ hàng có nhiều orders, nếu thanh toán cùng lúc thì case này sẽ xảy ra
public class OrderTransaction {

    @Id
    @GenericGenerator(name = "custom_id", strategy = "com.kientruchanoi.ecommerce.orderservicecore.util.CustomIdGenerator")
    @GeneratedValue(generator = "custom_id")
    private String id;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "transaction_id")
    private String transactionId;
}
