package com.kientruchanoi.ecommerce.orderservicecore.controller;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Wallet;
import com.kientruchanoi.ecommerce.orderservicecore.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/deposit/request")
    public ResponseEntity<BaseResponse<Wallet>> deposit(@RequestParam("amount") long amount) {
        return walletService.deposit(amount);
    }

    //số tiền cần thanh toán để hoàn tất nạp tiền vào ví
    @GetMapping("/deposit/pending")
    public ResponseEntity<BaseResponse<String>> pending() {
        return walletService.pendingAmountDeposit();
    }

    @PutMapping("/deposit/manual_submit/{userId}")
    public ResponseEntity<BaseResponse<Wallet>> manualSubmitDeposit(@PathVariable("userId") String userId) {
        return walletService.manualSubmitDeposit(userId);
    }

    @GetMapping("/detail")
    public ResponseEntity<BaseResponse<Wallet>> detail() {
        return walletService.detail();
    }
}
