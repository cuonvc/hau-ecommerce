package com.kientruchanoi.ecommerce.productservicecore.controller;

import com.kientruchanoi.ecommerce.baseservice.constant.PageConstant;
import com.kientruchanoi.ecommerce.baseservice.payload.response.BaseResponse;
import com.kientruchanoi.ecommerce.productservicecore.service.ProductService;
import com.kientruchanoi.ecommerce.productserviceshare.payload.request.ProductRequest;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.PageResponseProduct;
import com.kientruchanoi.ecommerce.productserviceshare.payload.response.ProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<ProductResponse>> create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BaseResponse<ProductResponse>> update(@PathVariable("id") String id,
                                                                @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse<String>> deleteById(@PathVariable("id") String id) {
        return productService.deleteById(id);
    }

    @PutMapping("/admin/restore/{id}")
    public ResponseEntity<BaseResponse<ProductResponse>> restoreById(@PathVariable("id") String id) {
        return productService.restore(id);
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<BaseResponse<ProductResponse>> details(@PathVariable("id") String id) {
        return productService.getById(id);
    }

    @GetMapping("/view/all")
    public ResponseEntity<BaseResponse<PageResponseProduct>> getAllActive(@RequestParam(value = "pageNo",
                                                                                  defaultValue = PageConstant.PAGE_NO, required = false) Integer pageNo,
                                                                          @RequestParam(value = "pageSize",
                                                                                  defaultValue = PageConstant.PAGE_SIZE, required = false) Integer pageSize,
                                                                          @RequestParam(value = "sortBy",
                                                                                  defaultValue = PageConstant.SORT_BY, required = false) String sortBy,
                                                                          @RequestParam(value = "sortDir",
                                                                                  defaultValue = PageConstant.SORT_DIR, required = false) String sortDir) {
        return productService.findAllByActive(pageNo, pageSize, sortBy, sortDir);
    }

    @GetMapping("/moderator/all")
    public ResponseEntity<BaseResponse<PageResponseProduct>> getAll(@RequestParam(value = "pageNo",
            defaultValue = PageConstant.PAGE_NO, required = false) Integer pageNo,
                                                                          @RequestParam(value = "pageSize",
                                                                                  defaultValue = PageConstant.PAGE_SIZE, required = false) Integer pageSize,
                                                                          @RequestParam(value = "sortBy",
                                                                                  defaultValue = PageConstant.SORT_BY, required = false) String sortBy,
                                                                          @RequestParam(value = "sortDir",
                                                                                  defaultValue = PageConstant.SORT_DIR, required = false) String sortDir) {
        return productService.findAll(pageNo, pageSize, sortBy, sortDir);
    }

    @GetMapping("/owner")
    public ResponseEntity<BaseResponse<PageResponseProduct>> getByOwner(@RequestParam(value = "pageNo",
                                                                                defaultValue = PageConstant.PAGE_NO, required = false) Integer pageNo,
                                                                        @RequestParam(value = "pageSize",
                                                                                defaultValue = PageConstant.PAGE_SIZE, required = false) Integer pageSize,
                                                                        @RequestParam(value = "sortBy",
                                                                                defaultValue = PageConstant.SORT_BY, required = false) String sortBy,
                                                                        @RequestParam(value = "sortDir",
                                                                                defaultValue = PageConstant.SORT_DIR, required = false) String sortDir) {
        return productService.findAllByOwner(pageNo, pageSize, sortBy, sortDir);
    }

}
