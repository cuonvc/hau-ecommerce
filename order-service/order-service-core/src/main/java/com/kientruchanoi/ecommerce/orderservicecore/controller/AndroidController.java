package com.kientruchanoi.ecommerce.orderservicecore.controller;

import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.orderservicecore.entity.Wallet;
import com.kientruchanoi.ecommerce.orderservicecore.request.PaymentSMS;
import com.kientruchanoi.ecommerce.orderservicecore.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order/wallet")
@RequiredArgsConstructor
@Slf4j
public class AndroidController {

    private final WalletService walletService;

//    @PostMapping("/deposit/submit")
//    public ResponseEntity<String> receivePayment(@RequestBody PaymentSMS paymentSMS) {
//        log.info("RECEIVED ON SERVER - {}", paymentSMS);
//        walletService.confirmPayment(paymentSMS);
//
//        return ResponseEntity.ok("Success");
//    }

    @PostMapping("/deposit/submit")
    public ResponseEntity<String> test(@RequestParam(value = "phone", required = false) String phone,
                                       @RequestParam(value = "text", required = false) String text,
                                       @RequestParam(value = "sim", required = false) String sim,
                                       @RequestParam(value = "device", required = false) String device) {
        log.info("RECEIVED ON SERVER - {} - {} - {} - {}", phone, text, sim, device);
        walletService.confirmPayment(phone, text, sim, device);
        return ResponseEntity.ok("OK");
    }
}
