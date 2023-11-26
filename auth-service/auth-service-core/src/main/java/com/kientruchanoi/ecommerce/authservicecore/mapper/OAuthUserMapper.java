package com.kientruchanoi.ecommerce.authservicecore.mapper;

import com.kientruchanoi.ecommerce.authservicecore.payload.OAuthUserInfo;
import com.kientruchanoi.ecommerce.authservicecore.payload.response.GithubResponseUser;
import com.kientruchanoi.ecommerce.authservicecore.payload.response.GoogleResponseUser;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface OAuthUserMapper {

    OAuthUserInfo toUserInfo(GoogleResponseUser user);

    @Mapping(source = "fullName", target = "firstName", qualifiedByName = "getFirstNameGithub")
    @Mapping(source = "fullName", target = "lastName", qualifiedByName = "getLastNameGithub")
    OAuthUserInfo toUserInfo(GithubResponseUser user);

    @Named("getFirstNameGithub")
    default String getFirstName(String fullName) {
        return fullName.split(" ")[0];
    }

    @Named("getLastNameGithub")
    default String getLastName(String fullName) {
        return fullName.split(" ")[1];
    }
}
