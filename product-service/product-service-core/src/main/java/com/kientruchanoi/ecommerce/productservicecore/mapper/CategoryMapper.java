package com.kientruchanoi.ecommerce.productservicecore.mapper;

import com.kientruchanoi.ecommerce.baseservice.constant.ConstantVariable;
import com.kientruchanoi.ecommerce.productservicecore.entity.Category;
import com.kientruchanoi.ecommerce.productserviceshare.payload.CategoryDto;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.CategoryResponse;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface CategoryMapper {

    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    Category dtoToEntity(CategoryDto categoryDto);

    void dtoToEntity(CategoryDto categoryDto, @MappingTarget Category category);

    CategoryDto entityToDto(Category category);

    @Mapping(source = "imageUrl", target = "imageUrl", qualifiedByName = "pathToUrl")
    CategoryResponse entityToResponse(Category category);

    @Named("pathToUrl")
    static String pathToUrl(String path) {
        if (path != null && path.contains("http")) {
            return path;
        }
        return ConstantVariable.BASE_RESOURCE_DOMAIN + path;
    }

}
