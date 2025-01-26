package com.serhat.security.repository;

import com.serhat.security.entity.Product;
import com.serhat.security.entity.User;
import com.serhat.security.entity.ShoppingCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingCardRepository extends JpaRepository<ShoppingCard,Long> {
    List<ShoppingCard> findByUser(User user);

    boolean existsByUserAndProduct(User user, Product product);

    void deleteByUserAndProduct(User user, Product product);
}
