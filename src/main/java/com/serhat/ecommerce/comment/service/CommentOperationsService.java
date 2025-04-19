package com.serhat.ecommerce.comment.service;

import com.serhat.ecommerce.comment.mapper.CommentMapper;
import com.serhat.ecommerce.comment.repository.CommentRepository;
import com.serhat.ecommerce.comment.entity.Comment;
import com.serhat.ecommerce.comment.dto.request.CommentRequest;
import com.serhat.ecommerce.comment.dto.response.CommentResponse;
import com.serhat.ecommerce.order.entity.Order;
import com.serhat.ecommerce.order.details.OrderDetailsService;
import com.serhat.ecommerce.product.entity.Product;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.product.service.ProductService;
import com.serhat.ecommerce.user.userS.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentOperationsService implements CommentOperationService {
    private final CommentRepository commentRepository;
    private final ProductService productService;
    private final OrderDetailsService orderDetailsService;
    private final UserService userService;
    private final CommentMapper commentMapper;
    private final CommentValidationService commentValidationService;


    @Override
    @Transactional
    public CommentResponse createComment(CommentRequest commentRequest) {
        User user = userService.getAuthenticatedUser();
        Product product = productService.getProductById(commentRequest.productId());
        Order order = orderDetailsService.findOrderById(commentRequest.orderId());

        commentValidationService.validateCommentCreation(order, product);

        Comment comment = commentMapper.toComment(user, product, order, commentRequest);
        commentRepository.save(comment);
        log.info("Comment created by user: {}", user.getUsername());

        return commentMapper.mapToCommentResponse(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        User user = userService.getAuthenticatedUser();
        Comment comment = commentValidationService.findCommentById(commentId);

        commentValidationService.validateCommentOwnership(comment, user);

        commentRepository.delete(comment);
        log.info("Comment deleted by user: {}", user.getUsername());
    }

}