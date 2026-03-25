package com.example.blogcntt.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {
    @NotBlank
    private String content;
    private Long postId;
}
