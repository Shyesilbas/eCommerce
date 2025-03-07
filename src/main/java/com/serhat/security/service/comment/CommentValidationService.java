package com.serhat.security.service.comment;

import com.serhat.security.entity.Comment;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.User;
import com.serhat.security.exception.CommentNotAllowedException;
import com.serhat.security.exception.CommentNotFoundForProductException;
import com.serhat.security.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentValidationService {
    private final CommentRepository commentRepository;

    public boolean isProductInUserOrder(Order order, Product product) {
        return order.getOrderItems().stream()
                .anyMatch(orderItem -> orderItem.getProduct().equals(product));
    }

    public Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundForProductException("Comment not found with ID: " + commentId));
    }

    public void validateCommentOwnership(Comment comment, User user) {
        if (!comment.getUser().equals(user)) {
            throw new CommentNotAllowedException("You are not authorized to modify this comment!");
        }
    }

    public void validateCommentCreation(Order order, Product product) {
        if (!isProductInUserOrder(order, product)) {
            throw new CommentNotAllowedException("You can only comment on products you have ordered.");
        }
    }
}