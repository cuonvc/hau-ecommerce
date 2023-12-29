package com.kientruchanoi.ecommerce.authservicecore.service.impl;

import com.kientruchanoi.ecommerce.authservicecore.config.CustomUserDetail;
import com.kientruchanoi.ecommerce.authservicecore.entity.DeliveryAddress;
import com.kientruchanoi.ecommerce.authservicecore.entity.User;
import com.kientruchanoi.ecommerce.authservicecore.exception.APIException;
import com.kientruchanoi.ecommerce.authservicecore.mapper.DeliveryMapper;
import com.kientruchanoi.ecommerce.authservicecore.repository.DeliveryAddressRepository;
import com.kientruchanoi.ecommerce.authservicecore.repository.UserRepository;
import com.kientruchanoi.ecommerce.authservicecore.service.DeliveryAddressService;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.DeliveryAddressResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryAddressServiceImpl implements DeliveryAddressService {

    private final DeliveryAddressRepository repository;
    private final ResponseFactory responseFactory;
    private final DeliveryMapper deliveryMapper;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<BaseResponse<DeliveryAddress>> insert(DeliveryAddress address) {
        boolean isDefault = false;
        int count = repository.countByUserId(getCurrentUserId());
        if (count < 1) {
            isDefault = true;
        }
        DeliveryAddress deliveryAddress = repository.save(DeliveryAddress.builder()
                .province(address.getProvince())
                .district(address.getDistrict())
                .ward(address.getWard())
                .detail(address.getDetail())
                .phone(address.getPhone())
                .recipientName(address.getRecipientName())
                .userId(getCurrentUserId())
                .isDefault(isDefault)
                .build());

        if (isDefault) {
            User user = userRepository.findById(getCurrentUserId())
                    .orElseThrow(() -> new APIException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống"));
            user.setDetailAddress(deliveryAddress.getWard() + ", "
                    + deliveryAddress.getProvince() + ", "
                    + deliveryAddress.getDistrict());

            userRepository.save(user);
        }
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
        deliveryAddress.setPhone(address.getPhone());
        deliveryAddress.setRecipientName(address.getRecipientName());
        return responseFactory.success("Success", repository.save(deliveryAddress));
    }

    @Override
    public ResponseEntity<BaseResponse<DeliveryAddress>> setDefault(String id) {

        DeliveryAddress deliveryAddress = repository.findByDefaultIsAndUserId(getCurrentUserId());
        if (deliveryAddress != null) {
            deliveryAddress.setDefault(false);
            repository.save(deliveryAddress);
        }

        DeliveryAddress newDefault = repository.findByIdAndUserId(id, getCurrentUserId())
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Địa chỉ không tồn tại"));
        newDefault.setDefault(true);
        newDefault = repository.save(newDefault);

        User user = userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new APIException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống"));
        user.setDetailAddress(newDefault.getWard() + ", "
                + newDefault.getProvince() + ", "
                + newDefault.getDistrict());
        userRepository.save(user);

        return responseFactory.success("Success", newDefault);
    }

    @Override
    public ResponseEntity<BaseResponse<String>> delete(String id) {
        DeliveryAddress address = repository.findByIdAndUserId(id, getCurrentUserId())
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Dịa chỉ không tồn tại hoặc bạn không dược phép truy cập"));

        repository.delete(address);
        return responseFactory.success("Success", "Xoá thành công");
    }

    @Override
    public ResponseEntity<BaseResponse<DeliveryAddressResponse>> detail(String id) {
        return responseFactory.success("Success", deliveryMapper.entityToResponse(
                repository.findById(id)
                        .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Địa chỉ không tồn tại"))
        ));
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
