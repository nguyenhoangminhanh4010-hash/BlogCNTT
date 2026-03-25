package com.example.blogcntt.repository;

import com.example.blogcntt.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestion_IdOrderByCreatedAtAsc(Long questionId);
}
