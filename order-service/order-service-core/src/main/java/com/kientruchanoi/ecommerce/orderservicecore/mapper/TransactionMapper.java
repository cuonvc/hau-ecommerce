package com.kientruchanoi.ecommerce.orderservicecore.mapper;

import com.kientruchanoi.ecommerce.orderservicecore.entity.Transaction;
import com.kientruchanoi.ecommerce.orderserviceshare.payload.response.TransactionResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface TransactionMapper {

    TransactionResponse entityToResponse(Transaction entity);
}
