package com.p14172tdd.domain.post.controller;

import com.p14172tdd.domain.post.dto.PostCommentDto;
import com.p14172tdd.domain.post.entity.Post;
import com.p14172tdd.domain.post.entity.PostComment;
import com.p14172tdd.domain.post.service.PostService;
import com.p14172tdd.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class ApiV1PostCommentController {
    private final PostService postService;

    record PostCommentWriteReqBody(
            @NotBlank String content
    ) {
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public RsData<PostCommentDto> write(
            @PathVariable long postId,
            @Valid @RequestBody PostCommentWriteReqBody reqBody
    ) {
        Post post = postService.findById(postId).get();

        PostComment comment = postService.writeComment(post, reqBody.content());

        return new RsData<>(
                "201-1",
                "%d번 댓글이 작성되었습니다.".formatted(comment.getId()),
                new PostCommentDto(comment)
        );
    }

    @DeleteMapping("/{id}")
    @Transactional
    public RsData<Void> delete(
            @PathVariable long postId,
            @PathVariable long id
    ) {
        Post post = postService.findById(postId).get();
        PostComment comment = post.findCommentById(id).get();

        postService.deleteComment(post, comment);

        return new RsData<>(
                "200-1",
                "%d번 댓글이 삭제되었습니다.".formatted(id),
                null
        );
    }
}
