package com.p14172tdd.domain.post.dto;

import com.p14172tdd.domain.post.entity.Post;

// 엔티티를 그대로 내보내지 않고, 필요한 칸만 담아서 응답한다
public record PostDto(long id, String title, String content) {
    public PostDto(Post post) {
        this(post.getId(), post.getTitle(), post.getContent());
    }
}
