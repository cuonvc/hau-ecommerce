package com.kientruchanoi.ecommerce.orderservicecore.service;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Order;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Wallet;
import com.kientruchanoi.ecommerce.orderservicecore.request.PaymentSMS;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.WalletCustomResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface WalletService {

    ResponseEntity<BaseResponse<Wallet>> deposit(Long amount);

    ResponseEntity<BaseResponse<String>> pendingAmount();

    ResponseEntity<BaseResponse<Wallet>> withdrawRequest(long amount);

    ResponseEntity<BaseResponse<Wallet>> submitWithdraw(String userId);

    ResponseEntity<BaseResponse<Wallet>> confirmWithdraw();

    void confirmPayment(PaymentSMS sms);

    Wallet customerRefund(Order order);

    Wallet sellerPay(Order order);

    ResponseEntity<BaseResponse<Wallet>> manualSubmitDeposit(String userId);

    ResponseEntity<BaseResponse<List<WalletCustomResponse>>> walletManage(String status);

    ResponseEntity<BaseResponse<Wallet>> detail();

    Wallet walletBuilder(String userId);

    void reduceBalanceByOrder(String userId, Double amount, List<String> orderIds);
    void plusBalanceByOrder(String userId, Double amount);
}
