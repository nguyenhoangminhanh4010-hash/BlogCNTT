package com.example.blogcntt.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @JsonIgnore
    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 20)
    private String role = "USER"; // "ADMIN" or "USER"

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;
}
