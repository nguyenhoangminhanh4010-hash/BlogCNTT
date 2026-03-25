package com.example.blogcntt.controller;

import com.example.blogcntt.entity.LikePost;
import com.example.blogcntt.entity.Post;
import com.example.blogcntt.entity.User;
import com.example.blogcntt.payload.request.PostRequest;
import com.example.blogcntt.payload.response.MessageResponse;
import com.example.blogcntt.repository.LikePostRepository;
import com.example.blogcntt.repository.PostRepository;
import com.example.blogcntt.repository.UserRepository;
import com.example.blogcntt.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikePostRepository likePostRepository;

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postRepository.findAllByOrderByCreatedAtDesc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Optional<Post> post = postRepository.findById(id);
        return post.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPost(@Valid @RequestBody PostRequest postRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }

        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setImageUrl(postRequest.getImageUrl());
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());

        postRepository.save(post);
        return ResponseEntity.ok(new MessageResponse("Post created successfully!"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @Valid @RequestBody PostRequest postRequest) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Post post = postOptional.get();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setImageUrl(postRequest.getImageUrl());

        postRepository.save(post);
        return ResponseEntity.ok(new MessageResponse("Post updated successfully!"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @jakarta.transaction.Transactional
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Delete all likes to prevent foreign key constraint violations
        likePostRepository.deleteByPost(postOpt.get());

        postRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Post deleted successfully!"));
    }

    @PostMapping("/{id}/like")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> likePost(@PathVariable Long id, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId()).orElse(null);

        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty() || user == null) {
            return ResponseEntity.notFound().build();
        }

        Post post = postOptional.get();

        if (likePostRepository.existsByPostAndUser(post, user)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User already liked this post!"));
        }

        LikePost likePost = new LikePost();
        likePost.setPost(post);
        likePost.setUser(user);
        likePostRepository.save(likePost);

        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);

        return ResponseEntity.ok(new MessageResponse("Post liked successfully!"));
    }
}
