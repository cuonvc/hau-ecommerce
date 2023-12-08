package com.kientruchanoi.ecommerce.orderservicecore.mapper;

import com.kientruchanoi.ecommerce.orderservicecore.entity.Order;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.request.OrderRequest;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.OrderResponse;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.OrderResponseDetail;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface OrderMapper {

//    Order requestToEntity(OrderRequest request);

    OrderResponse entityToResponse(Order entity);

    OrderResponseDetail entityToResponseDetail(Order entity);
}
