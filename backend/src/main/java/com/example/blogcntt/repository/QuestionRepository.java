package com.example.blogcntt.repository;

import com.example.blogcntt.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByIsApprovedTrueOrderByCreatedAtDesc();
    List<Question> findByIsApprovedFalseOrderByCreatedAtDesc();
    List<Question> findAllByOrderByCreatedAtDesc();
}
