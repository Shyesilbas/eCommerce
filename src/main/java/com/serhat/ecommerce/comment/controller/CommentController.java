package com.serhat.ecommerce.comment.controller;

import com.serhat.ecommerce.dto.request.CommentRequest;
import com.serhat.ecommerce.dto.response.AverageBrandRatingResponse;
import com.serhat.ecommerce.dto.response.AverageRatingResponse;
import com.serhat.ecommerce.dto.response.CommentResponse;
import com.serhat.ecommerce.comment.service.CommentDetailsService;
import com.serhat.ecommerce.comment.service.CommentOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentOperationService commentOperationService;
    private final CommentDetailsService commentDetailsService;

    @PostMapping("/post-comment")
    public ResponseEntity<CommentResponse> postComment(@RequestBody CommentRequest commentRequest){
        return ResponseEntity.ok(commentOperationService.createComment(commentRequest));
    }

    @GetMapping("/comments-by-product")
    public ResponseEntity<List<CommentResponse>> commentsForProduct(@RequestParam Long productId){
        return ResponseEntity.ok(commentDetailsService.getCommentsByProduct(productId));
    }

    @GetMapping("/comments-by-user")
    public ResponseEntity<List<CommentResponse>> commentsMadeByUser(){
        return ResponseEntity.ok(commentDetailsService.getCommentsByUser());
    }

    @GetMapping("/products/average-rating")
    public ResponseEntity<AverageRatingResponse> getAverageProductRating(@RequestParam Long productId) {
        return ResponseEntity.ok(commentDetailsService.getAverageProductRating(productId));
    }

    @GetMapping("/average-rating-for-brand")
    public ResponseEntity<AverageBrandRatingResponse> averageRatingForBrand(@RequestParam String brand){
        return ResponseEntity.ok(commentDetailsService.getAverageRatingForBrand(brand));
    }

    @DeleteMapping("/delete-comment")
    public void deleteComment(@RequestParam Long commentId ){
        commentOperationService.deleteComment(commentId);
    }

}
