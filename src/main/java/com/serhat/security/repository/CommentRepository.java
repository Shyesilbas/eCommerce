package com.serhat.security.repository;

import com.serhat.security.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

    List<Comment> findByProductProductId(Long productId);
    List<Comment> findByUserUserId(Long userId);
    List<Comment> findByProductProductIdAndRatingGreaterThanEqual(Long productId, int rating);

    List<Comment> findByProductProductIdAndRatingLessThanEqual(Long productId, int rating);

    List<Comment> findByProductBrand(String brand);
}
