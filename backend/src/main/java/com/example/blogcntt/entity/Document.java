package com.example.blogcntt.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String title;

    @Column(length = 500)
    private String link;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
