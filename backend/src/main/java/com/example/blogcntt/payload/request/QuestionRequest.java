package com.example.blogcntt.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuestionRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
