package com.serhat.ecommerce.favorites.repository;

import com.serhat.ecommerce.favorites.entity.Favorites;
import com.serhat.ecommerce.product.entity.Product;
import com.serhat.ecommerce.user.userS.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites,Long> {

    List<Favorites> findByUser(User user);
    boolean existsByUserAndProduct(User user, Product product);
    Page<Favorites> findByUser(User user, Pageable pageable);

    Optional<Favorites> findByUserAndProduct(User user, Product product);}
