package com.serhat.ecommerce.comment.service;

import com.serhat.ecommerce.comment.repository.CommentRepository;
import com.serhat.ecommerce.comment.entity.Comment;
import com.serhat.ecommerce.order.Order;
import com.serhat.ecommerce.product.entity.Product;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.comment.commentException.CommentNotAllowedException;
import com.serhat.ecommerce.comment.commentException.CommentNotFoundForProductException;
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