package com.kientruchanoi.hauecommerce.mapper;

import com.kientruchanoi.hauecommerce.entity.User;
import com.kientruchanoi.hauecommerce.payload.repsonse.UserResponse;
import com.kientruchanoi.hauecommerce.payload.request.ProfileRequest;
import com.kientruchanoi.hauecommerce.payload.request.RegRequest;
import com.kientruchanoi.hauecommerce.utils.Constants;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User requestToEntity(RegRequest userRequest);

    @Mapping(source = "avatarUrl", target = "avatarUrl", qualifiedByName = "pathToUrl")
    UserResponse entityToResponse(User user);

    User profileToEntity(ProfileRequest profileRequest, @MappingTarget User user);

    @Named("pathToUrl")
    static String pathToUrl(String path) {
        if (path != null && path.contains("http")) {
            return path;
        }
        return Constants.BASE_RESOURCE_DOMAIN + path;
    }
}
