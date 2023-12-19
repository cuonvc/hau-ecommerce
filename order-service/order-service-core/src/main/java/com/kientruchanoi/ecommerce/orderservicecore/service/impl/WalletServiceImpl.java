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
import com.kientruchanoi.ecommerce.orderservicecore.mapper.WalletMapper;
import com.kientruchanoi.ecommerce.orderservicecore.repository.OrderRepository;
import com.kientruchanoi.ecommerce.orderservicecore.repository.TransactionRepository;
import com.kientruchanoi.ecommerce.orderservicecore.repository.WalletRepository;
import com.kientruchanoi.ecommerce.orderservicecore.request.PaymentSMS;
import com.kientruchanoi.ecommerce.orderservicecore.service.CommonService;
import com.kientruchanoi.ecommerce.orderservicecore.service.WalletService;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.OrderStatus;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.PaymentStatus;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.TransactionType;
import com.kientruchanoi.ecommerce.orderserviceshare.enumerate.WalletStatus;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.WalletCustomResponse;
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
    private final WalletMapper walletMapper;

    @Override
    public ResponseEntity<BaseResponse<Wallet>> deposit(Long amount) {
        if (amount <= 0) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Số tiền không hợp lệ.");
        }

        String currentUserId = commonService.getCurrentUserId();
        Wallet wallet = walletRepository.findByUserId(currentUserId)
                .orElse(walletBuilder(currentUserId));

        if (!wallet.getStatus().equals(WalletStatus.NORMAL.name())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Bạn không thể nạp tiền do ví đang trong trạng thái chờ duyệt");
        }

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
        } else if (wallet.getStatus().equals(WalletStatus.WITHDRAW_REQUEST.name())) {
            message = "Bạn đang yêu cầu rút " + wallet.getBalanceTemporary() + "đ";
        } else if (wallet.getStatus().equals(WalletStatus.WITHDRAW_CONFIRM.name())) {
            message = "Hệ thống đã thực hiện yêu cầu rút " + wallet.getBalanceTemporary() + "đ của bạn, vui lòng xác nhận.";
        } else {
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
            wallet.setStatus(WalletStatus.NORMAL.name());
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
    public ResponseEntity<BaseResponse<Wallet>> withdrawRequest(long amount) {
        Wallet wallet = walletRepository.findByUserId(commonService.getCurrentUserId())
                .orElse(walletBuilder(commonService.getCurrentUserId()));
        wallet = walletRepository.save(wallet); //if happen for else case

        if (amount % 10 != 0 || amount <= 0) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Số tiền rút phải là bội của 10");
        } else if (amount > wallet.getBalance()) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Số dư không đủ");
        } else if (!wallet.getStatus().equals(WalletStatus.NORMAL.name())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Bạn không thể thực hiện rút tiền do ví đang trong trạng thái chờ duyệt");
        }

        wallet.setStatus(WalletStatus.WITHDRAW_REQUEST.name());
        wallet.setBalance(wallet.getBalance() - (double) amount);
        wallet.setBalanceTemporary(wallet.getBalanceTemporary() + amount);
        wallet.setModifiedDate(LocalDateTime.now());
        return responseFactory.success("Đã yêu cầu rút số tiền " + amount + "đ", walletRepository.save(wallet));
    }

    @Override
    public ResponseEntity<BaseResponse<Wallet>> submitWithdraw(String userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, "Không tìm thấy ví"));

        if (commonService.getCurrentUser().getGrantedAuthorities().get(0).equals("USER")) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Không được phép truy cập");
        }

        if (!wallet.getStatus().equals(WalletStatus.WITHDRAW_REQUEST.name())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Ví không có trong trạng thái yêu cầu rút");
        }
        wallet.setModifiedDate(LocalDateTime.now());
        wallet.setStatus(WalletStatus.WITHDRAW_CONFIRM.name());
        return responseFactory.success("Chờ người dùng xác nhận.", walletRepository.save(wallet));
    }

    @Override
    public ResponseEntity<BaseResponse<Wallet>> confirmWithdraw() {
        String currentUserId = commonService.getCurrentUserId();
        Wallet wallet = walletRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, "Lỗi hệ thống, ví không tồn tại"));

        if (!wallet.getStatus().equals(WalletStatus.WITHDRAW_CONFIRM.name())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Không thể xác nhận không không trong trạng thái chờ xác nhận :v");
        }

        wallet.setStatus(WalletStatus.NORMAL.name());
        wallet.setBalanceTemporary(0D);
        wallet.setModifiedDate(LocalDateTime.now());
        return responseFactory.success("Đã xác nhận rút tiền thành công.", walletRepository.save(wallet));
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

        if (!wallet.getStatus().equals(WalletStatus.DEPOSIT_PENDING.name())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Lỗi - ví người dùng không trong trạng thái chờ nạp tiền");
        }
        double newAmount = wallet.getBalanceTemporary();
        wallet.setBalance(wallet.getBalance() + newAmount);
        wallet.setBalanceTemporary(0D);
        wallet.setStatus(WalletStatus.NORMAL.name());
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
    public ResponseEntity<BaseResponse<List<WalletCustomResponse>>> walletManage(String status) {
        if (commonService.getCurrentUser().getGrantedAuthorities().get(0).equals("USER")) {
            throw new APIException(HttpStatus.UNAUTHORIZED, "Không được phép truy cập");
        }

        try {
            WalletStatus walletStatus = WalletStatus.valueOf(status.trim().toUpperCase()); //check enum name ok
            List<WalletCustomResponse> walletList = walletRepository.findAllByStatus(walletStatus.name())
                    .stream().map(e -> {
                        UserResponse userinfo = commonService.getUserInfo(e.getUserId());
                        WalletCustomResponse response = walletMapper.entityToResponse(e);
                        response.setName(userinfo.getFirstName() + " " + userinfo.getLastName());
                        response.setBankAccount(userinfo.getBankAccount());
                        return response;
                    }).toList();

            return responseFactory.success("Success", walletList);
        } catch (Exception e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Status không hợp lệ.");
        }
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
                .status(WalletStatus.NORMAL.name())
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .build();
    }
}
