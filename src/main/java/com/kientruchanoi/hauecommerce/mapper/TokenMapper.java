package com.kientruchanoi.hauecommerce.mapper;

import com.kientruchanoi.hauecommerce.entity.RefreshToken;
import com.kientruchanoi.hauecommerce.payload.dto.RefreshTokenDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface TokenMapper {

    RefreshTokenDto mapToDto(RefreshToken refreshToken);
}
