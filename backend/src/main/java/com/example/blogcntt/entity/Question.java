package com.example.blogcntt.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "question")
@Data
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Column(length = 50)
    private String status = "APPROVED";

    @Column(name = "approved")
    private Boolean isApproved = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password", "posts"})
    private User user;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("question")
    private List<Answer> answers;
}
