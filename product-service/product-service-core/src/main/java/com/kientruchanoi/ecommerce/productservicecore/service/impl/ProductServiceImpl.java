package com.kientruchanoi.ecommerce.productservicecore.service.impl;

import com.kientruchanoi.ecommerce.authserviceshare.payload.response.UserResponse;
import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import com.kientruchanoi.ecommerce.baseservice.payload.request.FileObjectRequest;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.baseservice.payload.response.ResponseFactory;
import com.kientruchanoi.ecommerce.productservicecore.configuration.CustomUserDetail;
import com.kientruchanoi.ecommerce.productservicecore.entity.Category;
import com.kientruchanoi.ecommerce.productservicecore.entity.Product;
import com.kientruchanoi.ecommerce.productservicecore.entity.ProductResource;
import com.kientruchanoi.ecommerce.productservicecore.exception.APIException;
import com.kientruchanoi.ecommerce.productservicecore.exception.ResourceNotFoundException;
import com.kientruchanoi.ecommerce.productservicecore.mapper.ProductMapper;
import com.kientruchanoi.ecommerce.productservicecore.repository.CategoryRepository;
import com.kientruchanoi.ecommerce.productservicecore.repository.ProductRepository;
import com.kientruchanoi.ecommerce.productservicecore.service.FileImageService;
import com.kientruchanoi.ecommerce.productservicecore.service.ProductResourceService;
import com.kientruchanoi.ecommerce.productservicecore.service.ProductService;
import com.kientruchanoi.ecommerce.productserviceshare.payload.request.ProductRequest;
import com.kientruchanoi.ecommerce.productserviceshare.payload.request.ProductResourceRequest;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.PageResponseProduct;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final String THUMB = "imageUrl";

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductResourceService resourceService;
    private final ProductMapper productMapper;
    private final ResponseFactory responseFactory;
    private final StreamBridge streamBridge;
    private final RestTemplate restTemplate;
    private final ProductResourceService productResourceService;
    private final FileImageService fileImageService;

    @Override
    public ResponseEntity<BaseResponse<ProductResponse>> create(ProductRequest request) {
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        request.getResources().forEach(image -> {
            if (!isBase64Image(image.getImageValue())) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Invalid base64 image");
            }
        });

        Set<Category> categories = request.getCategoryIds().stream()
                .map(categoryId -> categoryRepository.findByIdAndIsActive(categoryId, Status.ACTIVE)
                        .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId)))
                .collect(Collectors.toSet());

        Product product = productMapper.requestToEntity(request);
        product.setCategories(categories);
        product.setUserId(userDetail.getId());
        productRepository.save(product);

        List<ProductResource> resources = productResourceService.initResources(product, request.getResources());
        product.setResources(new HashSet<>(resources));

        ProductResponse response = productMapper.entityToResponse(productRepository.save(product));
        response.setResources(productResourceService.getImageUrls(product.getId()));

        return responseFactory.success("Tạo thành công", response);
    }

//    private List<String> handleMapResource(ProductRequest request, Product product, List<ProductResource> resources) {
//        int sizeRequest = request.getResources().size();
//        if (sizeRequest < 5) {
//            IntStream.range(0, 5 - sizeRequest)
//                    .forEach(item -> request.getResources().add(ProductResourceRequest.builder()
//                            .id(null)
//                            .imageValue("")
//                            .build()));
//        }
//
//        return IntStream.range(0, resources.size()).mapToObj(index -> {
//            String imageValue = request.getResources().get(index).getImageValue();
//            resources.get(index).setImageUrl(imageValue);
//            return uploadImage(product.getId(), resources.get(index).getId(), imageValue);
//        }).collect(Collectors.toList());
//    }

//    private String uploadImage(String productId, String resourceId, String imageValue) {
//        return fileImageService.saveImageFile(Base64.getDecoder().decode(imageValue));
//    }

    @Override
    public ResponseEntity<BaseResponse<ProductResponse>> update(String id, ProductRequest request) {
        CustomUserDetail userDetail = (CustomUserDetail)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        request.getResources().forEach(image -> {
            if (!isBase64Image(image.getImageValue())) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Invalid base64 image");
            }
        });

        Product product = productRepository.findByIdAndUserId(id, userDetail.getId())
                .orElseThrow(() -> new APIException(HttpStatus.UNAUTHORIZED, "Không được phép truy cập"));
        if (product.getIsActive().equals(Status.INACTIVE)) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Product is inactive");
        }

        Set<Category> categories = request.getCategoryIds().stream()
                .map(categoryId -> categoryRepository.findByIdAndIsActive(categoryId, Status.ACTIVE)
                        .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId)))
                .collect(Collectors.toSet());

//        resourceService.clearImagePath(product.getId());
        productMapper.requestToEntity(request, product);
        product.setCategories(categories);
        List<ProductResource> newResources = productResourceService.updateResources(product, request.getResources());
        product.setResources(new HashSet<>(newResources));

        return responseFactory.success("Cập nhật thành công",
                productMapper.entityToResponse(productRepository.save(product)));
    }

    private boolean isBase64Image(String data) {
        try {
            byte[] decoded = Base64.getDecoder().decode(data);
            return ImageIO.read(new ByteArrayInputStream(decoded)) != null;
        } catch (Exception e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Invalid base64 image");
        }
    }

    @Override
    public ResponseEntity<BaseResponse<ProductResponse>> getById(String id) {
        Product product = productRepository.findByIdAndIsActive(id, Status.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm", "id", id));
        ProductResponse response = productMapper.entityToResponse(product);
        response.setResources(productResourceService.getImageUrls(response.getId()));
        response.setUser(getUserInfo(product.getUserId()));

        return responseFactory.success("Success", response);
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<String>> deleteById(String id) {
        Product product = productRepository.findByIdAndIsActive(id, Status.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm", "id", id));

        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetail.getGrantedAuthorities().get(0).equals("USER") && !userDetail.getId().equals(product.getUserId())) {
            return responseFactory.fail(HttpStatus.UNAUTHORIZED, "Không được phép truy cập", null);
        }

        product.setIsActive(Status.INACTIVE);
        productRepository.save(product);
        return responseFactory.success("Xóa thành công", "Xóa thành công");
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<ProductResponse>> restore(String id) {
        Product product = productRepository.findByIdAndIsActive(id, Status.INACTIVE)
                .orElseThrow(() -> new APIException(HttpStatus.BAD_REQUEST, "Mặt hàng đang online hoặc không tồn tại"));

        product.setIsActive(Status.ACTIVE);

        return responseFactory.success("Khôi phục thành công", mappingResponse(productRepository.save(product)));
    }

    private ProductResponse mappingResponse(Product product) {
        ProductResponse response = productMapper.entityToResponse(product);
        response.setResources(productResourceService.getImageUrls(response.getId()));
        response.setUser(getUserInfo(product.getUserId()));

        return response;
    }

    private UserResponse getUserInfo(String userId) {
        return Optional
                .ofNullable(restTemplate.exchange(
                        "http://AUTH-SERVICE/api/auth/account/" + userId,  //http or https???
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<BaseResponse<UserResponse>>() {}
                ).getBody().getData())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }


    @Override
    public ResponseEntity<BaseResponse<PageResponseProduct>> findAllByActive(Integer pageNo, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Product> productPage = productRepository.findByIsActive(pageable, Status.ACTIVE);

        return responseFactory.success("Success", paging(productPage));
    }

    @Override
    public ResponseEntity<BaseResponse<PageResponseProduct>> findAll(Integer pageNo, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Product> productPage = productRepository.findAll(pageable);

        return responseFactory.success("Success", paging(productPage));
    }

    @Override
    public ResponseEntity<BaseResponse<List<ProductResponse>>> filterByKeyword(String keyword) {
        List<Product> resultSearch = productRepository.findByNameMatchesRegexOrBrandMatchesRegex(keyword, keyword);

        List<ProductResponse> responses = resultSearch.stream()
                .map(product -> {
                    ProductResponse response = productMapper.entityToResponse(product);
                    response.setResources(resourceService.getImageUrls(response.getId()));
                    response.setUser(getUserInfo(product.getUserId()));
                    return response;
                })
                .toList();

        return responseFactory.success("Ok", responses);
    }

    @Override
    public ResponseEntity<BaseResponse<PageResponseProduct>> findAllByOwner(Integer pageNo, Integer pageSize, String sortBy, String sortDir) {
        CustomUserDetail userDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Product> productPage = productRepository.findAllByUserIdAndIsActive(pageable, userDetail.getId(), Status.ACTIVE);

        return responseFactory.success("Success", paging(productPage));
    }

    @Override
    public ResponseEntity<BaseResponse<PageResponseProduct>> findAllByShop(String userId, Integer pageNo, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Product> productPage = productRepository.findAllByUserIdAndIsActive(pageable, userId, Status.ACTIVE);

        return responseFactory.success("Success", paging(productPage));
    }

    private PageResponseProduct paging(Page<Product> productPage) {
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(product -> {
                    ProductResponse response = productMapper.entityToResponse(product);
                    response.setResources(resourceService.getImageUrls(response.getId()));
                    response.setUser(getUserInfo(product.getUserId()));
                    return response;
                })
                .toList();

        return (PageResponseProduct) PageResponseProduct.builder()
                .pageNo(productPage.getNumber())
                .pageSize(productResponses.size())
                .content(productResponses)
                .totalPages(productPage.getTotalPages())
                .totalItems((int) productPage.getTotalElements())
                .last(productPage.isLast())
                .build();
    }
}
