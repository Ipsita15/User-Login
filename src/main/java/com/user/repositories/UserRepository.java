package com.user.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.user.model.UserModel;


@Repository
public interface UserRepository extends JpaRepository<UserModel, String> {
    UserModel findByEmail(String email);
    boolean existsByEmail(String email);
    
//    String findPasswordHashByEmail(String email);
    
    @Query("SELECT u FROM UserModel u WHERE u.email = :email")
    UserModel getPasswordHashByEmail (@Param("email") String email);
    
}
