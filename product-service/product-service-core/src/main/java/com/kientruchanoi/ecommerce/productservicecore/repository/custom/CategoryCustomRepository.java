package com.kientruchanoi.ecommerce.productservicecore.repository.custom;


import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
import com.kientruchanoi.ecommerce.productservicecore.entity.Category;

import java.util.List;

public interface CategoryCustomRepository {
    Category findByName(String name);

    Category findByIdAndStatus(String id, String status);

//    List<Category> findByParentIdAndStatus(String id, Status status);
}
