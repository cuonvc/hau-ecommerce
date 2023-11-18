package com.kientruchanoi.hauecommerce.mapper;

import com.kientruchanoi.hauecommerce.entity.Category;
import com.kientruchanoi.hauecommerce.payload.dto.CategoryDto;
import com.kientruchanoi.hauecommerce.payload.repsonse.CategoryResponse;
import com.kientruchanoi.hauecommerce.utils.Constants;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface CategoryMapper {

    @Mapping(target = "isActive", ignore = true)
    Category dtoToEntity(CategoryDto categoryDto);

    void dtoToEntity(CategoryDto categoryDto, @MappingTarget Category category);

    CategoryDto entityToDto(Category category);

    @Mapping(source = "imageUrl", target = "imageUrl", qualifiedByName = "pathToUrl")
    CategoryResponse entityToResponse(Category category);

    @Named("pathToUrl")
    static String pathToUrl(String path) {
//        if (path != null) {
//            return path;
//        }
        return Constants.BASE_RESOURCE_DOMAIN + path;
    }

}
