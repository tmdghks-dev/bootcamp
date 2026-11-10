package com.back.domain.post.post.repository;

import com.back.domain.post.post.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("2번 글 조회")
    void t1() {
        Post post2 = postRepository.findById(2).get();

        assertThat(post2.getTitle()).isEqualTo("제목 2");
        assertThat(post2.getContent()).isEqualTo("내용 2");
    }

    @Test
    @DisplayName("글 생성")
    void t2() {
        Post post = new Post("제목 new", "내용 new");
        assertThat(post.getId()).isEqualTo(0);

        postRepository.save(post);

        assertThat(post.getId()).isGreaterThan(0);
        assertThat(post.getTitle()).isEqualTo("제목 new");
        assertThat(post.getContent()).isEqualTo("내용 new");
    }

    @Test
    @DisplayName("글 개수 조회")
    void t3() {
        long count = postRepository.count();

        assertThat(count).isEqualTo(4);
    }
}
