package com.kientruchanoi.ecommerce.orderservicecore.repository;

import com.kientruchanoi.ecommerce.orderservicecore.entity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, String> {

    @Query("SELECT cp FROM CartProduct cp " +
            "WHERE cp.cartId = :cartId ")
    List<CartProduct> findAllByCartId(String cartId);

    @Query("SELECT cp FROM CartProduct cp " +
            "WHERE cp.cartId = :cartId AND cp.productId = :productId")
    List<CartProduct> findByCartAndProduct(String cartId, String productId);

    @Modifying
    @Transactional
    @Query("DELETE CartProduct cp " +
            "WHERE cp.cartId = :cartId AND cp.productId = :productId")
    void deleteByCartAndProduct(String cartId, String productId);

    @Transactional
    @Modifying
    @Query("DELETE CartProduct cp " +
            "WHERE cp.cartId = :cartId")
    void deleteAllByCart(String cartId);

    @Query("SELECT COUNT(cp.id) FROM CartProduct cp " +
            "WHERE cp.cartId = :cartId")
    int countProductInCart(String cartId);

    @Query("SELECT cp.quantity FROM CartProduct cp " +
            "WHERE cp.cartId = :cartId AND cp.productId = :productId")
    int countQuantityOfProductInCart(String cartId, String productId);
}
