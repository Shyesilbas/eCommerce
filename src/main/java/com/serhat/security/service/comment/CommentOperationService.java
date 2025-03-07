package com.serhat.security.service.comment;

import com.serhat.security.dto.request.CommentRequest;
import com.serhat.security.dto.response.CommentResponse;

public interface CommentOperationService {
    CommentResponse createComment(CommentRequest commentRequest);
    void deleteComment(Long commentId);
}
