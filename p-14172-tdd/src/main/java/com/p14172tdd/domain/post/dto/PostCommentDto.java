package com.p14172tdd.domain.post.dto;

import com.p14172tdd.domain.post.entity.PostComment;

public record PostCommentDto(long id, long postId, String content) {
    public PostCommentDto(PostComment comment) {
        this(comment.getId(), comment.getPost().getId(), comment.getContent());
    }
}
