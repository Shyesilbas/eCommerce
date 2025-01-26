package com.serhat.security.repository;

import com.serhat.security.entity.Favorites;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites,Long> {

    List<Favorites> findByUser(User user);
    boolean existsByUserAndProduct(User user, Product product);
    void deleteByUserAndProduct(User user, Product product);

    Optional<Favorites> findByUserAndProduct(User user, Product product);}
