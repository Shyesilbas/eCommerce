package com.serhat.ecommerce.comment.service;

import com.serhat.ecommerce.dto.request.CommentRequest;
import com.serhat.ecommerce.dto.response.CommentResponse;

public interface CommentOperationService {
    CommentResponse createComment(CommentRequest commentRequest);
    void deleteComment(Long commentId);
}
