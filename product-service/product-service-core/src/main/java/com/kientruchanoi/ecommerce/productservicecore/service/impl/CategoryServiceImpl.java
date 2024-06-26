package com.kientruchanoi.ecommerce.productservicecore.service.impl;

import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import com.kientruchanoi.ecommerce.productservicecore.entity.Category;
import com.kientruchanoi.ecommerce.productservicecore.exception.APIException;
import com.kientruchanoi.ecommerce.productservicecore.exception.ResourceNotFoundException;
import com.kientruchanoi.ecommerce.productservicecore.mapper.CategoryMapper;
import com.kientruchanoi.ecommerce.productservicecore.repository.CategoryRepository;
import com.kientruchanoi.ecommerce.productservicecore.service.CategoryService;
import com.kientruchanoi.ecommerce.productservicecore.service.FileImageService;
import com.kientruchanoi.ecommerce.productserviceshare.payload.CategoryDto;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.CategoryResponse;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.PageResponseCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final String CATEGORY_IMAGE_REQUEST = "category-image-request";

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ResponseFactory responseFactory;
    private final StreamBridge streamBridge;
    private final FileImageService fileImageService;

    @Override
    public ResponseEntity<BaseResponse<CategoryResponse>> create(CategoryDto dto) {
        validateCategory("name", dto.getName());
        Category category = categoryMapper.dtoToEntity(dto);
        return responseFactory.success("Success",
                categoryMapper.entityToResponse(categoryRepository.save(category)));
    }

    @Override
    public ResponseEntity<BaseResponse<CategoryResponse>> update(CategoryDto categoryDto) {
        Category category = validateCategory("id", categoryDto.getId());
        validateCategory("name", categoryDto.getName());

        categoryMapper.dtoToEntity(categoryDto, category);
        category = categoryRepository.save(category);
        CategoryResponse response = categoryMapper.entityToResponse(category);
        return responseFactory.success("Success", response);
    }

    private boolean isBase64Image(String data) {
        if (data != null) {
            try {
                byte[] decoded = Base64.getDecoder().decode(data);
                ImageIO.read(new ByteArrayInputStream(decoded));
                return true;
            } catch (IOException e) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Base64 Image invalid");
            }
        }
        return false;
    }

    private Category validateCategory(String field, String value) {
        return switch (field) {
            case "name" -> {
                if (categoryRepository.findByNameAndIsActive(value, Status.ACTIVE.name()).isPresent()) {
                    throw new APIException(HttpStatus.BAD_REQUEST, "Category '" + value + "' đã tồn tại hoặc tên không hợp lệ");
                }
                yield null;
            }

            case "id" -> categoryRepository.findByIdAndIsActive(value, Status.ACTIVE.name())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", value));

            default -> throw new APIException(HttpStatus.BAD_REQUEST, "Lỗi không xác định, liên hệ Admin");
        };
    }

    @Override
    public ResponseEntity<BaseResponse<CategoryResponse>> getById(String id) {
        Category entity = validateCategory("id", id);
        CategoryResponse response = categoryMapper.entityToResponse(entity);

        return responseFactory.success("Success", response);
    }

    @Override
    public ResponseEntity<BaseResponse<PageResponseCategory>> getAllActive(Integer pageNo, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Category> categories = categoryRepository.findAllByIsActive(pageable, Status.ACTIVE.name());
        return responseFactory.success("Success", paging(categories));
    }

    @Override
    public ResponseEntity<BaseResponse<PageResponseCategory>> getAll(Integer pageNo, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Category> categories = categoryRepository.findAll(pageable);

        return responseFactory.success("Success", paging(categories));
    }

    @Override
    public ResponseEntity<BaseResponse<String>> delete(String id) {
        Category category = validateCategory("id", id);

        category.setIsActive(Status.INACTIVE.name());
        categoryRepository.save(category);
        return responseFactory.success("Success", "Đã xóa " + category.getName());
    }

    @Override
    public ResponseEntity<BaseResponse<CategoryDto>> restore(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        category.setIsActive(Status.ACTIVE.name());
        category = categoryRepository.save(category);

        return responseFactory.success("Success", categoryMapper.entityToDto(category));
    }

    private PageResponseCategory paging(Page<Category> categoryPage) {
        List<Category> categories = categoryPage.getContent();

        List<CategoryResponse> responseList = categories.stream()
                .map(categoryMapper::entityToResponse)
                .toList();

        return (PageResponseCategory) PageResponseCategory.builder()
                .pageNo(categoryPage.getNumber())
                .pageSize(responseList.size())
                .content(responseList)
                .totalPages(categoryPage.getTotalPages())
                .totalItems((int) categoryPage.getTotalElements())
                .last(categoryPage.isLast())
                .build();
    }
}
