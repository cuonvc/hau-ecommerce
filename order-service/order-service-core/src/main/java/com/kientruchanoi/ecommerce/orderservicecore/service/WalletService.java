package com.kientruchanoi.ecommerce.orderservicecore.service;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Order;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Wallet;
import com.kientruchanoi.ecommerce.orderservicecore.request.PaymentSMS;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface WalletService {

    ResponseEntity<BaseResponse<Wallet>> deposit(Long amount);

    ResponseEntity<BaseResponse<String>> pendingAmountDeposit();

    void confirmPayment(PaymentSMS sms);

    void customerRefund(Order order);

    ResponseEntity<BaseResponse<Wallet>> manualSubmitDeposit(String userId);

    ResponseEntity<BaseResponse<Wallet>> detail();

    Wallet walletBuilder(String userId);

    void reduceBalanceByOrder(String userId, Double amount, List<String> orderIds);
    void plusBalanceByOrder(String userId, Double amount);
}
