package com.kientruchanoi.ecommerce.authservicecore.controller;

import com.kientruchanoi.ecommerce.authservicecore.entity.DeliveryAddress;
import com.kientruchanoi.ecommerce.authservicecore.service.DeliveryAddressService;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.DeliveryAddressResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryAddressService deliveryAddressService;

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<DeliveryAddress>> create(@RequestBody DeliveryAddress address) {
        return deliveryAddressService.insert(address);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BaseResponse<DeliveryAddress>> update(@PathVariable("id") String id,
                                                                @RequestBody DeliveryAddress address) {
        return deliveryAddressService.update(id, address);
    }

    @PutMapping("/default-set/{id}")
    public ResponseEntity<BaseResponse<DeliveryAddress>> setDefault(@PathVariable("id") String id) {
        return deliveryAddressService.setDefault(id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable("id") String id) {
        return deliveryAddressService.delete(id);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<BaseResponse<DeliveryAddressResponse>> detail(@PathVariable("id") String id) {
        return deliveryAddressService.detail(id);
    }

    @GetMapping("/list")
    public ResponseEntity<BaseResponse<List<DeliveryAddress>>> list() {
        return deliveryAddressService.getByOwner();
    }
}
