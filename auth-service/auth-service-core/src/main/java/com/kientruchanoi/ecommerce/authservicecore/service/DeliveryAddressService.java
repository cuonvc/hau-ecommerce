package com.kientruchanoi.ecommerce.authservicecore.service;

import com.ctc.wstx.shaded.msv_core.driver.textui.ReportErrorHandler;
import com.kientruchanoi.ecommerce.authservicecore.entity.DeliveryAddress;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

public interface DeliveryAddressService {
    ResponseEntity<BaseResponse<DeliveryAddress>> insert(DeliveryAddress address);

    ResponseEntity<BaseResponse<DeliveryAddress>> update(String id, DeliveryAddress address);

    ResponseEntity<BaseResponse<String>> delete(String id);

    ResponseEntity<BaseResponse<DeliveryAddress>> detail(String id);

    ResponseEntity<BaseResponse<List<DeliveryAddress>>> getByOwner();
}
