package com.kientruchanoi.ecommerce.orderservicecore.service.impl;

import com.kientruchanoi.ecommerce.authserviceshare.payload.enumerate.Role;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import com.kientruchanoi.ecommerce.orderservicecore.configuration.CustomUserDetail;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Order;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Transaction;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Wallet;
import com.kientruchanoi.ecommerce.orderservicecore.exception.APIException;
import com.kientruchanoi.ecommerce.orderservicecore.repository.OrderRepository;
import com.kientruchanoi.ecommerce.orderservicecore.repository.TransactionRepository;
import com.kientruchanoi.ecommerce.orderservicecore.repository.WalletRepository;
import com.kientruchanoi.ecommerce.orderservicecore.request.PaymentSMS;
import com.kientruchanoi.ecommerce.orderservicecore.service.CommonService;
import com.kientruchanoi.ecommerce.orderservicecore.service.WalletService;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.PaymentStatus;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.TransactionType;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.WalletStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final CommonService commonService;
    private final ResponseFactory responseFactory;
    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;

    @Override
    public ResponseEntity<BaseResponse<Wallet>> deposit(Long amount) {
        if (amount <= 0) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Số tiền không hợp lệ.");
        }

        String currentUserId = commonService.getCurrentUserId();
        Wallet wallet = walletRepository.findByUserId(currentUserId)
                .orElse(walletBuilder(currentUserId));

        wallet.setBalanceTemporary((double) amount);
        wallet.setStatus(WalletStatus.DEPOSIT_PENDING.name());
        wallet.setSmsFormat(currentUserId + "_" + amount);
        return responseFactory.success("Nạp tiền thanh công, vui lòng chuyển " + amount + "VNĐ cho quản trị viên",
                walletRepository.save(wallet));
    }

    @Override
    public ResponseEntity<BaseResponse<String>> pendingAmountDeposit() {
        String currentUserId = commonService.getCurrentUserId();
        Wallet wallet = walletRepository.findByUserId(currentUserId)
                .orElse(walletBuilder(currentUserId));
        wallet = walletRepository.save(wallet);

        String message;
        if (wallet.getStatus().equals(WalletStatus.DEPOSIT_PENDING.name())) {
            message = "Bạn chưa xác nhận nạp tiền, nhắn tin (chuyển khoản) theo cú pháp "
                    + wallet.getSmsFormat()
                    + " để hoàn tất quá trình nap tiền và mua sắp với nhiều ưu đãi tại HAU-Ecommerce";
        } else  {
            message = "Hãy nạp tiền vào tài khoản để trải nghiệm mua sắm trực tuyến tại HAU-Ecommerce";
        }
        return responseFactory.success("Success", message);
    }

    @Override
    public void confirmPayment(PaymentSMS sms) {
        String[] message = sms.getContent().split("_");  //có thể sẽ lỗi trên android
        long amount = Long.parseLong(message[1]);
        String userId = message[0];

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, "Lỗi hệ thống..."));

        if (amount == wallet.getBalanceTemporary()) {

            wallet.setBalance(wallet.getBalance() + amount);
            wallet.setBalanceTemporary(0D);
            wallet.setSmsFormat(null);
            wallet.setStatus(WalletStatus.DEPOSIT_PAID.name());
            wallet.setModifiedDate(LocalDateTime.now());
            wallet = walletRepository.save(wallet);

            transactionRepository.save(
                    Transaction.builder()
                            .walletId(wallet.getId())
                            .type(TransactionType.DEPOSIT.name())
                            .amount(amount)
                            .balance(wallet.getBalance())
                            .description("Nạp tiền vào ví")
                            .createdDate(LocalDateTime.now())
                            .build()
            );
        }
    }

    @Override
    public Wallet customerRefund(Order order) {
        Wallet wallet = walletRepository.findByUserId(order.getCustomerId())
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Lỗi hệ thống - không tìm thấy ví khách hanng"));
        wallet.setBalance(wallet.getBalance() + order.getAmount());
        wallet.setTotalAmountPaid(wallet.getTotalAmountPaid() - order.getAmount());
        wallet.setModifiedDate(LocalDateTime.now());

        return walletRepository.save(wallet);
    }

    @Override
    public Wallet sellerPay(Order order) {
        Wallet wallet = walletRepository.findByUserId(order.getSellerId())
                .orElse(walletBuilder(order.getSellerId()));

        wallet.setBalance(wallet.getBalance() + order.getAmount());
        wallet.setModifiedDate(LocalDateTime.now());

        return walletRepository.save(wallet);
    }

    @Override
    public ResponseEntity<BaseResponse<Wallet>> manualSubmitDeposit(String userId) {
        if (commonService.getCurrentUser().getGrantedAuthorities().get(0).equals("USER")) {
            throw new APIException(HttpStatus.UNAUTHORIZED, "Không được phép truy cập.");
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Tài khoản không tồn tại"));

        double newAmount = wallet.getBalanceTemporary();
        wallet.setBalance(wallet.getBalance() + newAmount);
        wallet.setBalanceTemporary(0D);
        wallet.setStatus(WalletStatus.DEPOSIT_PAID.name());
        wallet.setSmsFormat(null);
        wallet.setModifiedDate(LocalDateTime.now());

        transactionRepository.save(
                Transaction.builder()
                        .walletId(wallet.getId())
                        .type(TransactionType.DEPOSIT.name())
                        .amount((long) newAmount)
                        .balance(wallet.getBalance())
                        .description("Nạp tiền vào ví")
                        .createdDate(LocalDateTime.now())
                        .build()
        );

        return responseFactory.success("Đã xác nhận nạp tiền.", walletRepository.save(wallet));
    }

    @Override
    public ResponseEntity<BaseResponse<Wallet>> detail() {
        String userId = commonService.getCurrentUserId();
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElse(walletBuilder(userId));
        return responseFactory.success("Success", walletRepository.save(wallet));
    }

    @Override
    public void reduceBalanceByOrder(String userId, Double amount, List<String> orderIds) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElse(walletBuilder(userId));
        wallet.setBalance(wallet.getBalance() - amount);
        wallet.setTotalAmountPaid(wallet.getTotalAmountPaid() + amount);
        walletRepository.save(wallet);

        orderIds.forEach(id -> {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, "Lỗi hệ thống....hm"));
            order.setPaymentStatus(PaymentStatus.PAID.name());
            orderRepository.save(order);
        });
    }

    @Override
    public void plusBalanceByOrder(String userId, Double amount) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElse(walletBuilder(userId));
        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);
    }

    @Override
    public Wallet walletBuilder(String userId) {
        return Wallet.builder()
                .userId(userId)
                .balance(0D)
                .balanceTemporary(0D)
                .totalAmountPaid(0D)
                .status(WalletStatus.DEPOSIT_PAID.name())
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .build();
    }
}
