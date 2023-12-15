package com.kientruchanoi.ecommerce.authservicecore.mapper;

import com.kientruchanoi.ecommerce.authservicecore.entity.DeliveryAddress;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.DeliveryAddressResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface DeliveryMapper {

    DeliveryAddressResponse entityToResponse(DeliveryAddress entity);
}
