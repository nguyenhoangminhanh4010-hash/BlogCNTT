package com.example.blogcntt.controller;

import com.example.blogcntt.entity.Comment;
import com.example.blogcntt.entity.Post;
import com.example.blogcntt.entity.User;
import com.example.blogcntt.payload.request.CommentRequest;
import com.example.blogcntt.payload.response.MessageResponse;
import com.example.blogcntt.repository.CommentRepository;
import com.example.blogcntt.repository.PostRepository;
import com.example.blogcntt.repository.UserRepository;
import com.example.blogcntt.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentRepository.findByPostIdOrderByCreatedAtDesc(postId));
    }

    @PostMapping
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentRequest commentRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId()).orElse(null);

        Optional<Post> postOptional = postRepository.findById(commentRequest.getPostId());

        if (postOptional.isEmpty() || user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Post or User not found!"));
        }

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setPost(postOptional.get());
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());

        commentRepository.save(comment);

        return ResponseEntity.ok(new MessageResponse("Comment added successfully!"));
    }
}
