package com.example.blogcntt.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "post")
@Data
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(length = 50)
    private String status = "APPROVED";

    private Integer likes = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password", "posts"})
    private User user;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Comment> comments;
}
