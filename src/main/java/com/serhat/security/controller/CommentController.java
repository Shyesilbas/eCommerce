package com.serhat.security.controller;

import com.serhat.security.dto.request.CommentRequest;
import com.serhat.security.dto.response.AverageBrandRatingResponse;
import com.serhat.security.dto.response.AverageRatingResponse;
import com.serhat.security.dto.response.CommentResponse;
import com.serhat.security.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<CommentResponse> postComment(@RequestBody CommentRequest commentRequest , HttpServletRequest request){
        return ResponseEntity.ok(commentService.createComment(request, commentRequest));
    }

    @GetMapping("/comments-by-product")
    public ResponseEntity<List<CommentResponse>> commentsForProduct(@RequestParam Long productId){
        return ResponseEntity.ok(commentService.getCommentsByProduct(productId));
    }

    @GetMapping("/comments-by-user")
    public ResponseEntity<List<CommentResponse>> commentsMadeByUser(HttpServletRequest request){
        return ResponseEntity.ok(commentService.getCommentsByUser(request));
    }

    @GetMapping("/products/average-rating")
    public ResponseEntity<AverageRatingResponse> getAverageProductRating(@RequestParam Long productId) {
        return ResponseEntity.ok(commentService.getAverageProductRating(productId));
    }

    @GetMapping("/most-helpful-comments")
    public ResponseEntity<List<CommentResponse>> mostHelpfulComments(@RequestParam Long productId){
        return ResponseEntity.ok(commentService.getMostHelpfulComments(productId));
    }

    @GetMapping("/least-helpful-comments")
    public ResponseEntity<List<CommentResponse>> leastHelpfulComments(@RequestParam Long productId){
        return ResponseEntity.ok(commentService.getLeastHelpfulComments(productId));
    }

    @GetMapping("/average-rating-for-brand")
    public ResponseEntity<AverageBrandRatingResponse> averageRatingForBrand(@RequestParam String brand){
        return ResponseEntity.ok(commentService.getAverageRatingForBrand(brand));
    }

    @DeleteMapping("/delete-comment")
    public void deleteComment(@RequestParam Long commentId , HttpServletRequest request){
        commentService.deleteComment(commentId, request);
    }

}
