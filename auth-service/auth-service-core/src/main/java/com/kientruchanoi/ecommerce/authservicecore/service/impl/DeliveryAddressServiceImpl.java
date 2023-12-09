package com.kientruchanoi.ecommerce.authservicecore.service.impl;

import com.kientruchanoi.ecommerce.authservicecore.config.CustomUserDetail;
import com.kientruchanoi.ecommerce.authservicecore.entity.DeliveryAddress;
import com.kientruchanoi.ecommerce.authservicecore.exception.APIException;
import com.kientruchanoi.ecommerce.authservicecore.repository.DeliveryAddressRepository;
import com.kientruchanoi.ecommerce.authservicecore.service.DeliveryAddressService;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryAddressServiceImpl implements DeliveryAddressService {

    private final DeliveryAddressRepository repository;
    private final ResponseFactory responseFactory;

    @Override
    public ResponseEntity<BaseResponse<DeliveryAddress>> insert(DeliveryAddress address) {
        DeliveryAddress deliveryAddress = repository.save(DeliveryAddress.builder()
                .province(address.getProvince())
                .district(address.getDistrict())
                .ward(address.getWard())
                .detail(address.getDetail())
                .userId(getCurrentUserId())
                .build());
        return responseFactory.success("Success", deliveryAddress);
    }

    @Override
    public ResponseEntity<BaseResponse<DeliveryAddress>> update(String id, DeliveryAddress address) {
        DeliveryAddress deliveryAddress = repository.findById(id)
                .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, "Địa chỉ không tồn tại"));

        if (!deliveryAddress.getUserId().equals(getCurrentUserId())) {
            throw new APIException(HttpStatus.UNAUTHORIZED, "Không dđược phép truy cập");
        }

        deliveryAddress.setProvince(address.getProvince());
        deliveryAddress.setDistrict(address.getDistrict());
        deliveryAddress.setWard(address.getWard());
        deliveryAddress.setDetail(address.getDetail());
        return responseFactory.success("Success", repository.save(deliveryAddress));
    }

    @Override
    public ResponseEntity<BaseResponse<String>> delete(String id) {
        DeliveryAddress address = repository.findByIdAndUserId(id, getCurrentUserId())
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Dịa chỉ không tồn tại hoặc bạn không dược phép truy cập"));

        repository.delete(address);
        return responseFactory.success("Success", "Xoá thành công");
    }

    @Override
    public ResponseEntity<BaseResponse<DeliveryAddress>> detail(String id) {
        return responseFactory.success("Success", repository.findById(id)
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Địa chỉ không tồn tại")));
    }

    @Override
    public ResponseEntity<BaseResponse<List<DeliveryAddress>>> getByOwner() {
        return responseFactory.success("Success", repository.findByUserId(getCurrentUserId()));
    }

    private String getCurrentUserId() {
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return userDetail.getId();
    }
}
