package com.kientruchanoi.ecommerce.authservicecore.mapper;

import com.kientruchanoi.ecommerce.authservicecore.entity.User;
import com.kientruchanoi.ecommerce.authserviceshare.payload.request.ProfileRequest;
import com.kientruchanoi.ecommerce.authserviceshare.payload.request.RegRequest;
import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.baseservice.constant.ConstantVariable;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User requestToEntity(RegRequest userRequest);

    UserResponse entityToResponse(User user);

    User profileToEntity(ProfileRequest profileRequest, @MappingTarget User user);

//    @Named("pathToUrl")
//    static String pathToUrl(String path) {
//        if (path != null && path.contains("http")) {
//            return path;
//        }
//        return ConstantVariable.BASE_RESOURCE_DOMAIN + path;
//    }
}
