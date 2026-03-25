package com.example.blogcntt.controller;

import com.example.blogcntt.entity.Answer;
import com.example.blogcntt.entity.Question;
import com.example.blogcntt.entity.User;
import com.example.blogcntt.payload.request.AnswerRequest;
import com.example.blogcntt.payload.response.MessageResponse;
import com.example.blogcntt.repository.AnswerRepository;
import com.example.blogcntt.repository.QuestionRepository;
import com.example.blogcntt.repository.UserRepository;
import com.example.blogcntt.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<Answer>> getAnswersByQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok(answerRepository.findByQuestion_IdOrderByCreatedAtAsc(questionId));
    }

    @PostMapping
    public ResponseEntity<?> createAnswer(@Valid @RequestBody AnswerRequest answerRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId()).orElse(null);

        Optional<Question> questionOptional = questionRepository.findById(answerRequest.getQuestionId());

        if (questionOptional.isEmpty() || user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Question or User not found!"));
        }

        Answer answer = new Answer();
        answer.setContent(answerRequest.getContent());
        answer.setQuestion(questionOptional.get());
        answer.setUser(user);
        answer.setCreatedAt(LocalDateTime.now());

        answerRepository.save(answer);

        return ResponseEntity.ok(new MessageResponse("Answer added successfully!"));
    }
}
