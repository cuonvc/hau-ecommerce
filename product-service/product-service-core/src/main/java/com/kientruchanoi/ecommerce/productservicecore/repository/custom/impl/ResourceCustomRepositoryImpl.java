//package com.kientruchanoi.ecommerce.productservicecore.repository.custom.impl;
//
//import com.kientruchanoi.ecommerce.productservicecore.entity.Product;
//import com.kientruchanoi.ecommerce.productservicecore.entity.ProductResource;
//import com.kientruchanoi.ecommerce.productservicecore.repository.custom.ResourceCustomRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//@RequiredArgsConstructor
//public class ResourceCustomRepositoryImpl implements ResourceCustomRepository {
//
//    private final MongoTemplate mongoTemplate;
//
//    @Override
//    public List<ProductResource> findByProductId(String id) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("product").is(Optional.ofNullable(
//                mongoTemplate.findById(id, Product.class)
//        )));
//
//        return mongoTemplate.find(query, ProductResource.class);
//    }
//}
