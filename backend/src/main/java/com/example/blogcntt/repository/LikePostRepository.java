package com.example.blogcntt.repository;

import com.example.blogcntt.entity.LikePost;
import com.example.blogcntt.entity.Post;
import com.example.blogcntt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikePostRepository extends JpaRepository<LikePost, Long> {
    boolean existsByPostAndUser(Post post, User user);
    void deleteByPost(Post post);
}
