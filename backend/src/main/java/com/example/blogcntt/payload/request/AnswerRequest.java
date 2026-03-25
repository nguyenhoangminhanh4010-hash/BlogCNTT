package com.example.blogcntt.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnswerRequest {
    @NotBlank
    private String content;
    private Long questionId;
}
