package com.serhat.security.controller;

import com.serhat.security.dto.request.CommentRequest;
import com.serhat.security.dto.response.AverageBrandRatingResponse;
import com.serhat.security.dto.response.AverageRatingResponse;
import com.serhat.security.dto.response.CommentResponse;
import com.serhat.security.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/post-comment")
    public ResponseEntity<CommentResponse> postComment(@RequestBody CommentRequest commentRequest){
        return ResponseEntity.ok(commentService.createComment(commentRequest));
    }

    @GetMapping("/comments-by-product")
    public ResponseEntity<List<CommentResponse>> commentsForProduct(@RequestParam Long productId){
        return ResponseEntity.ok(commentService.getCommentsByProduct(productId));
    }

    @GetMapping("/comments-by-user")
    public ResponseEntity<List<CommentResponse>> commentsMadeByUser(){
        return ResponseEntity.ok(commentService.getCommentsByUser());
    }

    @GetMapping("/products/average-rating")
    public ResponseEntity<AverageRatingResponse> getAverageProductRating(@RequestParam Long productId) {
        return ResponseEntity.ok(commentService.getAverageProductRating(productId));
    }

    @GetMapping("/average-rating-for-brand")
    public ResponseEntity<AverageBrandRatingResponse> averageRatingForBrand(@RequestParam String brand){
        return ResponseEntity.ok(commentService.getAverageRatingForBrand(brand));
    }

    @DeleteMapping("/delete-comment")
    public void deleteComment(@RequestParam Long commentId ){
        commentService.deleteComment(commentId);
    }

}
