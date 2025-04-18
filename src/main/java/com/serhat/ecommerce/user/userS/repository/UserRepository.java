package com.serhat.ecommerce.user.userS.repository;

import com.serhat.ecommerce.user.userS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
   Optional<User>  findByUsername(String username);

   Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmailOrUsernameOrPhone(String email, String username, String phone);
}
