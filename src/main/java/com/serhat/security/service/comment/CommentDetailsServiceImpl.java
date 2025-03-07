package com.serhat.security.service.comment;

import com.serhat.security.dto.response.AverageBrandRatingResponse;
import com.serhat.security.dto.response.AverageRatingResponse;
import com.serhat.security.dto.response.CommentResponse;
import com.serhat.security.entity.Comment;
import com.serhat.security.entity.User;
import com.serhat.security.exception.CommentNotFoundForBrandException;
import com.serhat.security.exception.CommentNotFoundForProductException;
import com.serhat.security.exception.CommentNotFoundForUserException;
import com.serhat.security.component.mapper.CommentMapper;
import com.serhat.security.repository.CommentRepository;
import com.serhat.security.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentDetailsServiceImpl implements CommentDetailsService{
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final CommentMapper commentMapper;

    public AverageRatingResponse getAverageProductRating(Long productId) {
        List<Comment> comments = commentRepository.findByProductProductId(productId);
        if (comments.isEmpty()) {
            log.warn("No comments found for product with ID: {}", productId);
            throw new CommentNotFoundForProductException("No comments found for this product.");
        }

        double averageRating = comments.stream()
                .mapToDouble(Comment::getRating)
                .average()
                .orElse(0.0);

        return new AverageRatingResponse(
                averageRating,
                comments.get(0).getProduct().getName(),
                comments.get(0).getProduct().getBrand(),
                comments.get(0).getProduct().getProductCode()
        );
    }

    public List<CommentResponse> getCommentsByProduct(Long productId) {
        List<Comment> comments = commentRepository.findByProductProductId(productId);
        if (comments.isEmpty()) {
            log.warn("No comments found for product with ID: {}", productId);
            throw new CommentNotFoundForProductException("No comments found for this product.");
        }
        return comments.stream()
                .map(commentMapper::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    public List<CommentResponse> getCommentsByUser() {
        User user = userService.getAuthenticatedUser();
        List<Comment> comments = commentRepository.findByUserUserId(user.getUserId());
        if (comments.isEmpty()) {
            log.warn("No comments found for user: {}", user.getUsername());
            throw new CommentNotFoundForUserException("No comments found for this user.");
        }
        return comments.stream()
                .map(commentMapper::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    public AverageBrandRatingResponse getAverageRatingForBrand(String brand) {
        List<Comment> comments = commentRepository.findByProductBrand(brand);
        if (comments.isEmpty()) {
            log.warn("No comments found for products of brand: {}", brand);
            throw new CommentNotFoundForBrandException("No comments found for this brand.");
        }

        double averageRating = comments.stream()
                .mapToDouble(Comment::getRating)
                .average()
                .orElse(0.0);

        return new AverageBrandRatingResponse(averageRating, brand);
    }
}