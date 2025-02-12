package com.serhat.security.service;

import com.serhat.security.dto.request.CommentRequest;
import com.serhat.security.dto.response.AverageBrandRatingResponse;
import com.serhat.security.dto.response.AverageRatingResponse;
import com.serhat.security.dto.response.CommentResponse;
import com.serhat.security.entity.Comment;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.User;
import com.serhat.security.exception.*;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.mapper.CommentMapper;
import com.serhat.security.repository.CommentRepository;
import com.serhat.security.repository.OrderRepository;
import com.serhat.security.repository.ProductRepository;
import com.serhat.security.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final TokenInterface tokenInterface;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentResponse createComment(HttpServletRequest request, CommentRequest commentRequest) {
        User user = tokenInterface.getUserFromToken(request);
        Product product = productRepository.findById(commentRequest.productId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        Order order = orderRepository.findById(commentRequest.orderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (!isProductInUserOrder(order, product)) {
            throw new CommentNotAllowedException("You can only comment on products you have ordered.");
        }

        Comment comment = Comment.builder()
                .user(user)
                .product(product)
                .order(order)
                .content(commentRequest.content())
                .rating(commentRequest.rating())
                .createdAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);
        log.info("Comment created by user: {}", user.getUsername());

        return commentMapper.mapToCommentResponse(comment);
    }

    public AverageRatingResponse getAverageProductRating(Long productId) {
        List<Comment> comments = commentRepository.findByProductProductId(productId);
        if (comments.isEmpty()) {
            log.warn("No comments found for product with ID: {}", productId);
            throw new CommentNotFoundForProductException("No comments found for this product.");
        }

        Product product = comments.get(0).getProduct();


     double averageRating = comments.stream()
                .mapToDouble(Comment::getRating)
                .average()
                .orElse(0.0);

        return new AverageRatingResponse(
                averageRating,
                product.getName(),
                product.getBrand(),
                product.getProductCode()
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

    public List<CommentResponse> getCommentsByUser(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        List<Comment> comments = commentRepository.findByUserUserId(user.getUserId());
        if (comments.isEmpty()) {
            log.warn("No comments found for user: {}", user.getUsername());
            throw new CommentNotFoundForUserException("No comments found for this user.");
        }
        return comments.stream()
                .map(commentMapper::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    public List<CommentResponse> getMostHelpfulComments(Long productId) {
        List<Comment> comments = commentRepository.findByProductProductIdAndRatingGreaterThanEqual(productId, 4);
        if (comments.isEmpty()) {
            throw new CommentNotFoundForProductException("No highly rated comments found for this product.");
        }
        return comments.stream()
                .map(commentMapper::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    public List<CommentResponse> getLeastHelpfulComments(Long productId) {
        List<Comment> comments = commentRepository.findByProductProductIdAndRatingLessThanEqual(productId, 2);
        if (comments.isEmpty()) {
            throw new CommentNotFoundForProductException("No low-rated comments found for this product.");
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

        Product product = comments.get(0).getProduct();

        return new AverageBrandRatingResponse(
                averageRating,
                brand
        );
    }
    @Transactional
    public void deleteComment(Long commentId, HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().equals(user)) {
            throw new RuntimeException("You are not authorized to delete this comment!");
        }

        commentRepository.delete(comment);
        log.info("Comment deleted by user: {}", user.getUsername());
    }

    private boolean isProductInUserOrder(Order order, Product product) {
        return order.getOrderItems().stream()
                .anyMatch(orderItem -> orderItem.getProduct().equals(product));
    }


}
