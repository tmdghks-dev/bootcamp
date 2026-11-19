package com.back.domain.post.postComment.entity;

import com.back.domain.member.member.entity.Member;
import com.back.domain.post.post.entity.Post;
import com.back.global.globalExceptionHandler.AccessDeniedException;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
public class PostComment extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    private Member author;
    @ManyToOne(fetch = LAZY)
    private Post post;
    private String content;

    public PostComment(Member author, Post post, String content) {
        this.author = author;
        this.post = post;
        this.content = content;
    }

    // 작성자 본인이 아니면 403
    public void checkActorCanDelete(Member actor) {
        if (author.getId() != actor.getId()) throw new AccessDeniedException();
    }

    public void checkActorCanModify(Member actor) {
        if (author.getId() != actor.getId()) throw new AccessDeniedException();
    }

    public void modify(String content) {
        this.content = content;
    }
}