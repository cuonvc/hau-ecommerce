package com.kientruchanoi.ecommerce.productservicecore.mapper;

import com.kientruchanoi.ecommerce.baseservice.constant.ConstantVariable;
import com.kientruchanoi.ecommerce.productservicecore.entity.Product;
import com.kientruchanoi.ecommerce.productserviceshare.payload.request.ProductRequest;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResponse;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface ProductMapper {

    Product requestToEntity(ProductRequest productRequest);

    void requestToEntity(ProductRequest productRequest, @MappingTarget Product product);

//    @Mapping(source = "imageUrl", target = "imageUrl", qualifiedByName = "pathToUrl")
    ProductResponse entityToResponse(Product product);

    @Named("pathToUrl")
    static String pathToUrl(String path) {
        if (path != null && path.contains("http")) {
            return path;
        }
        return ConstantVariable.BASE_RESOURCE_DOMAIN + path;
    }
}
