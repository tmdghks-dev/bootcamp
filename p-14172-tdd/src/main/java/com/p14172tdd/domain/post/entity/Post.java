package com.p14172tdd.domain.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    // 글을 지우면 댓글도 같이 지워지고, 목록에서 뺀 댓글은 DB에서도 지워진다
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> comments = new ArrayList<>();

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public PostComment addComment(String content) {
        PostComment comment = new PostComment(this, content);
        comments.add(comment);
        return comment;
    }

    public Optional<PostComment> findCommentById(long id) {
        return comments.stream()
                .filter(comment -> comment.getId() == id)
                .findFirst();
    }

    public void deleteComment(PostComment comment) {
        comments.remove(comment);
    }
}
