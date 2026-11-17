package com.p14172tdd;

import com.p14172tdd.domain.post.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class P14172TddApplicationTests {
    @Autowired
    private PostService postService;

    @Test
    void 샘플_데이터_글3_댓글5() {
        assertThat(postService.count()).isEqualTo(3);

        int commentCount = 0;
        for (long id = 1; id <= 3; id++) {
            commentCount += postService.findById(id).get().getComments().size();
        }
        assertThat(commentCount).isEqualTo(5);
    }
}
