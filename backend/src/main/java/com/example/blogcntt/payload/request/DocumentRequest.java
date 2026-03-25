package com.example.blogcntt.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DocumentRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String link;
}
