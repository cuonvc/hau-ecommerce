package com.kientruchanoi.hauecommerce.service.impl;

import com.kientruchanoi.hauecommerce.entity.Category;
import com.kientruchanoi.hauecommerce.enumerate.Status;
import com.kientruchanoi.hauecommerce.exception.APIException;
import com.kientruchanoi.hauecommerce.exception.ResourceNotFoundException;
import com.kientruchanoi.hauecommerce.mapper.CategoryMapper;
import com.kientruchanoi.hauecommerce.payload.dto.CategoryDto;
import com.kientruchanoi.hauecommerce.payload.repsonse.BaseResponse;
import com.kientruchanoi.hauecommerce.payload.repsonse.CategoryResponse;
import com.kientruchanoi.hauecommerce.payload.repsonse.ResponseFactory;
import com.kientruchanoi.hauecommerce.repository.CategoryRepository;
import com.kientruchanoi.hauecommerce.service.CategoryService;
import com.kientruchanoi.hauecommerce.utils.FileUploadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ResponseFactory responseFactory;
    private final FileUploadUtils fileUploadUtils;

    @Override
    public ResponseEntity<BaseResponse<CategoryResponse>> create(CategoryDto dto) {
        validateCategory("name", dto.getName());

        if (!isBase64Image(dto.getImageValue())) {
            return responseFactory.fail(HttpStatus.BAD_REQUEST, "Base64 image invalid", null);
        }
        Category category =  categoryMapper.dtoToEntity(dto);
        category = categoryRepository.save(category); //save to get id
        category.setImageUrl(storeImage(category.getId(), dto.getImageValue()));
        category = categoryRepository.save(category);


        CategoryResponse response = categoryMapper.entityToResponse(category);

        return responseFactory.success("Success", response);
    }

    @Override
    public ResponseEntity<BaseResponse<CategoryResponse>> update(CategoryDto categoryDto) {
        Category category = validateCategory("id", categoryDto.getId());
        validateCategory("name", categoryDto.getName());

        if (!isBase64Image(categoryDto.getImageValue())) {
            return responseFactory.fail(HttpStatus.BAD_REQUEST, "Base64 image invalid", null);
        }

        categoryMapper.dtoToEntity(categoryDto, category);
        category.setImageUrl(storeImage(category.getId(), categoryDto.getImageValue()));
        category = categoryRepository.save(category);
        CategoryResponse response = categoryMapper.entityToResponse(category);

        return responseFactory.success("Success", response);
    }

    private boolean isBase64Image(String data) {
        try {
            byte[] decoded = Base64.getDecoder().decode(data);
            return ImageIO.read(new ByteArrayInputStream(decoded)) != null;
        } catch (IOException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Base64 Image invalid");
        }
    }

    private String storeImage(String categoryId, String base64Image) {
        String uploadDir = "image/category/" + categoryId;
        Path path = fileUploadUtils.saveFile(uploadDir, Base64.getDecoder().decode(base64Image));
        return path.toString().replace("\\", "/");
    }

    private Category validateCategory(String field, String value) {
        return switch (field) {
            case "name" -> {
                if (categoryRepository.findByNameAndIsActive(value, Status.ACTIVE).isPresent()) {
                    throw new APIException(HttpStatus.BAD_REQUEST, "Category '" + value + "' đã tồn tại hoặc tên không hợp lệ");
                }
                yield null;
            }

            case "id" -> categoryRepository.findByIdAndIsActive(value, Status.ACTIVE)
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

//    @Override
//    public ResponseEntity<BaseResponse<PageResponseCategory>> getAllActive(Integer pageNo, Integer pageSize, String sortBy, String sortDir) {
//        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
//                ? Sort.by(sortBy).ascending()
//                : Sort.by(sortBy).descending();
//
//        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
//
//        Page<Category> categories = categoryRepository.findAllByIsActive(pageable, Status.ACTIVE);
//        return responseFactory.success("Success", paging(categories));
//    }

//    @Override
//    public ResponseEntity<BaseResponse<PageResponseCategory>> getAll(Integer pageNo, Integer pageSize, String sortBy, String sortDir) {
//        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
//                ? Sort.by(sortBy).ascending()
//                : Sort.by(sortBy).descending();
//
//        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
//        Page<Category> categories = categoryRepository.findAll(pageable);
//
//        return responseFactory.success("Success", paging(categories));
//    }

    @Override
    public ResponseEntity<BaseResponse<String>> delete(String id) {
        Category category = validateCategory("id", id);

        category.setIsActive(Status.INACTIVE);
        categoryRepository.save(category);
        return responseFactory.success("Success", "Đã xóa " + category.getName());
    }

    @Override
    public ResponseEntity<BaseResponse<CategoryDto>> restore(String id) {
        Category category = validateCategory("id", id);
        category.setIsActive(Status.ACTIVE);
        category = categoryRepository.save(category);

        return responseFactory.success("Success", categoryMapper.entityToDto(category));
    }

//    private PageResponseCategory paging(Page<Category> categoryPage) {
//        List<Category> categories = categoryPage.getContent();
//        Map<String, CategoryResponse> responseMap = new HashMap<>();
//
//        categories.forEach(category
//                -> responseMap.put(category.getId(), categoryMapper.entityToResponse(category))
//        );
//
//        List<Category> topLevel = new ArrayList<>();
//        categories.forEach(category -> {
//            String parentId = category.getParentId();
//            if (parentId == null || parentId.isEmpty()) {
//                topLevel.add(category);
//            } else {
//                CategoryResponse response = responseMap.get(parentId);
//                if (response != null) {
//                    response.addChildren(categoryMapper.entityToResponse(category));
//                }
//            }
//        });
//
//        List<CategoryResponse> responseList = topLevel.stream()
//                .map(entity -> {
//                    CategoryResponse response = categoryMapper.entityToResponse(entity);
//                    response.setChildren(getResponseWithChildren(entity.getId()));
//                    return response;
//                })
//                .toList();
//
//        return (PageResponseCategory) PageResponseCategory.builder()
//                .pageNo(categoryPage.getNumber())
//                .pageSize(responseList.size())
//                .content(responseList)
//                .totalPages(categoryPage.getTotalPages())
//                .totalItems((int) categoryPage.getTotalElements())
//                .last(categoryPage.isLast())
//                .build();
//    }
}
