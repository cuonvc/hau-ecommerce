package com.kientruchanoi.ecommerce.orderservicecore.service.impl;

import com.kientruchanoi.ecommerce.orderservicecore.entity.VietQrApi;
import com.kientruchanoi.ecommerce.orderservicecore.repository.VietQrApiRepository;
import com.kientruchanoi.ecommerce.orderservicecore.service.SystemService;
import jakarta.transaction.SystemException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {

    private final VietQrApiRepository vqrApiRepository;

    @Override
    public VietQrApi getVietQr() {
        List<VietQrApi> vietQrs = vqrApiRepository.findAll();
        return vietQrs.size() == 1 ? vietQrs.get(0) : null;
    }
}
