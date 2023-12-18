package com.kientruchanoi.ecommerce.orderservicecore.controller;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Wallet;
import com.kientruchanoi.ecommerce.orderservicecore.service.CommonService;
import com.kientruchanoi.ecommerce.orderservicecore.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final CommonService commonService;

    @PostMapping("/deposit/request")
    public ResponseEntity<BaseResponse<Wallet>> deposit(@RequestParam("amount") long amount) {
        return walletService.deposit(amount);
    }

    //số tiền cần thanh toán để hoàn tất nạp tiền vào ví hoặc số tiền yêu cầu rút
    @GetMapping("/pending")
    public ResponseEntity<BaseResponse<String>> pending() {
        return walletService.pendingAmountDeposit();
    }

    //admin
    @PutMapping("/deposit/manual_submit/{userId}")
    public ResponseEntity<BaseResponse<Wallet>> manualSubmitDeposit(@PathVariable("userId") String userId) {
        return walletService.manualSubmitDeposit(userId);
    }

    @PostMapping("/withdraw/request")
    public ResponseEntity<BaseResponse<Wallet>> withdrawRequest(@RequestParam("amount") long amount) {
        return walletService.withdrawRequest(amount);
    }

    //admin
    @PutMapping("/withdraw/submit/{userId}")
    public ResponseEntity<BaseResponse<Wallet>> submitWithdraw(@PathVariable String userId) {
        return walletService.submitWithdraw(userId);
    }

    @PutMapping("/withdraw/confirm")
    public ResponseEntity<BaseResponse<Wallet>> confirmWithdraw() {
        return walletService.confirmWithdraw();
    }

    @GetMapping("/detail")
    public ResponseEntity<BaseResponse<Wallet>> detail() {
        return walletService.detail();
    }
}
