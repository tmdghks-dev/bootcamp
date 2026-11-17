package com.p14172tdd.domain.post.service;

import com.p14172tdd.domain.post.entity.Post;
import com.p14172tdd.domain.post.entity.PostComment;
import com.p14172tdd.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public long count() {
        return postRepository.count();
    }

    public Post write(String title, String content) {
        return postRepository.save(new Post(title, content));
    }

    public Optional<Post> findById(long id) {
        return postRepository.findById(id);
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    public PostComment writeComment(Post post, String content) {
        PostComment comment = post.addComment(content);
        // 바로 INSERT 시켜서 댓글 번호(id)를 받아 둔다
        postRepository.flush();
        return comment;
    }

    public void deleteComment(Post post, PostComment comment) {
        post.deleteComment(comment);
    }
}
