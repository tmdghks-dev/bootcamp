package com.p14172tdd.domain.post.controller;

import com.p14172tdd.domain.post.dto.PostDto;
import com.p14172tdd.domain.post.entity.Post;
import com.p14172tdd.domain.post.service.PostService;
import com.p14172tdd.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApiV1PostController {
    private final PostService postService;

    record PostWriteReqBody(
            @NotBlank String title,
            @NotBlank String content
    ) {
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public RsData<PostDto> write(@Valid @RequestBody PostWriteReqBody reqBody) {
        Post post = postService.write(reqBody.title(), reqBody.content());

        return new RsData<>(
                "201-1",
                "%d번 글이 작성되었습니다.".formatted(post.getId()),
                new PostDto(post)
        );
    }
}
