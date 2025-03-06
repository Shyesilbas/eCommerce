package com.serhat.security.component.mapper;

import com.serhat.security.dto.response.CommentResponse;
import com.serhat.security.entity.Comment;
import org.springframework.stereotype.Component;

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
}
