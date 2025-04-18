package com.serhat.ecommerce.comment.service;

import com.serhat.ecommerce.dto.response.AverageBrandRatingResponse;
import com.serhat.ecommerce.dto.response.AverageRatingResponse;
import com.serhat.ecommerce.dto.response.CommentResponse;

import java.util.List;

public interface CommentDetailsService {
    AverageRatingResponse getAverageProductRating(Long productId);
    List<CommentResponse> getCommentsByProduct(Long productId);
    List<CommentResponse> getCommentsByUser();
    AverageBrandRatingResponse getAverageRatingForBrand(String brand);
}
