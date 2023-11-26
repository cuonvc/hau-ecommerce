//package com.kientruchanoi.ecommerce.productservicecore.repository.custom.impl;
//
//import com.kientruchanoi.ecommerce.baseservice.constant.enumerate.Status;
//import com.kientruchanoi.ecommerce.productservicecore.entity.Category;
//import com.kientruchanoi.ecommerce.productservicecore.repository.custom.CategoryCustomRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//@RequiredArgsConstructor
//public class CategoryCustomRepositoryImpl implements CategoryCustomRepository {
//
//    private final MongoTemplate mongoTemplate;
//    private final Query query;
//
//    @Override
//    public Category findByName(String name) {
//        query.addCriteria(Criteria.where("name").is(name));
//        return mongoTemplate.findOne(query, Category.class);
//    }
//
//    @Override
//    public Category findByIdAndStatus(String id, Status status) {
//        query.addCriteria(Criteria.where("id").is(id))
//                .addCriteria(Criteria.where("isActive").is(status));
//        return mongoTemplate.findOne(query, Category.class);
//    }
//
////    @Override
////    public List<Category> findByParentIdAndStatus(String parentId, Status status) {
////        query.addCriteria(Criteria.where("parentId").is(parentId))
////                .addCriteria(Criteria.where("isActive").is(status));
////        return mongoTemplate.find(query, Category.class);
////    }
//
//}
