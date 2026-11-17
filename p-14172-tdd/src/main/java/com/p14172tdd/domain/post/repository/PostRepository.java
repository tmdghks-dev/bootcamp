package com.p14172tdd.domain.post.repository;

import com.p14172tdd.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
