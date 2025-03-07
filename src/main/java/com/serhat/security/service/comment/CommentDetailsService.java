package com.serhat.security.service.comment;

import com.serhat.security.dto.response.AverageBrandRatingResponse;
import com.serhat.security.dto.response.AverageRatingResponse;
import com.serhat.security.dto.response.CommentResponse;

import java.util.List;

public interface CommentDetailsService {
    AverageRatingResponse getAverageProductRating(Long productId);
    List<CommentResponse> getCommentsByProduct(Long productId);
    List<CommentResponse> getCommentsByUser();
    AverageBrandRatingResponse getAverageRatingForBrand(String brand);
}
