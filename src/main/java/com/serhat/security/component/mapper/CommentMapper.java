package com.serhat.security.component.mapper;

import com.serhat.security.dto.request.CommentRequest;
import com.serhat.security.dto.response.CommentResponse;
import com.serhat.security.entity.Comment;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentMapper {
    public CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .userId(comment.getUser().getUserId())
                .username(comment.getUser().getUsername())
                .productId(comment.getProduct().getProductId())
                .productName(comment.getProduct().getName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .rating(comment.getRating())
                .build();
    }

    public Comment toComment(User user, Product product, Order order, CommentRequest commentRequest) {
        return Comment.builder()
                .user(user)
                .product(product)
                .order(order)
                .content(commentRequest.content())
                .rating(commentRequest.rating())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
