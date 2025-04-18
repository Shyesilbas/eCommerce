package com.serhat.ecommerce.sCard.repository;

import com.serhat.ecommerce.product.entity.Product;
import com.serhat.ecommerce.sCard.entity.ShoppingCard;
import com.serhat.ecommerce.user.userS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingCardRepository extends JpaRepository<ShoppingCard,Long> {
    List<ShoppingCard> findByUser(User user);

    boolean existsByUserAndProduct(User user, Product product);

    void deleteByUserAndProduct(User user, Product product);

    Optional<ShoppingCard> findByUserAndProduct(User user, Product product);

    int countByUser(User user);
}
