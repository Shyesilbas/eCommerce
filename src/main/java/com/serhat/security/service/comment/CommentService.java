package com.serhat.security.service.comment;

import com.serhat.security.dto.request.CommentRequest;
import com.serhat.security.dto.response.AverageBrandRatingResponse;
import com.serhat.security.dto.response.AverageRatingResponse;
import com.serhat.security.dto.response.CommentResponse;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.Product;

import java.util.List;

public interface CommentService {
    CommentResponse createComment( CommentRequest commentRequest);
    AverageRatingResponse getAverageProductRating(Long productId);
    List<CommentResponse> getCommentsByProduct(Long productId);
    List<CommentResponse> getCommentsByUser();
    AverageBrandRatingResponse getAverageRatingForBrand(String brand);
    void deleteComment(Long commentId);
    boolean isProductInUserOrder(Order order, Product product);
}
