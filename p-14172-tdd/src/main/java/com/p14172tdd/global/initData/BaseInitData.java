package com.p14172tdd.global.initData;

import com.p14172tdd.domain.post.entity.Post;
import com.p14172tdd.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {
    private final PostService postService;

    // 자기 자신을 프록시로 받아야 work() 의 @Transactional 이 적용된다
    @Autowired
    @Lazy
    private BaseInitData self;

    @Bean
    public ApplicationRunner baseInitDataApplicationRunner() {
        return args -> self.work();
    }

    @Transactional
    public void work() {
        if (postService.count() > 0) return;

        Post post1 = postService.write("제목 1", "내용 1");
        Post post2 = postService.write("제목 2", "내용 2");
        postService.write("제목 3", "내용 3");

        postService.writeComment(post1, "댓글 1-1");
        postService.writeComment(post1, "댓글 1-2");
        postService.writeComment(post1, "댓글 1-3");
        postService.writeComment(post2, "댓글 2-1");
        postService.writeComment(post2, "댓글 2-2");
    }
}
