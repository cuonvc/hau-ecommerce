package com.kientruchanoi.ecommerce.authservicecore.repository;

import com.kientruchanoi.ecommerce.authservicecore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    List<User> findAllByRole(String role);

}
