package com.kientruchanoi.ecommerce.notificationservicecore.mapper;

import com.kientruchanoi.ecommerce.notificationservicecore.entity.Notification;
import com.kientruchanoi.ecommerce.notificationserviceshare.payload.response.NotificationResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface NotificationMapper {

    NotificationResponse entityToResponse(Notification notification);
}
