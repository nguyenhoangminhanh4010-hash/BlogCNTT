package com.example.blogcntt.controller;

import com.example.blogcntt.entity.Question;
import com.example.blogcntt.entity.User;
import com.example.blogcntt.payload.request.QuestionRequest;
import com.example.blogcntt.payload.response.MessageResponse;
import com.example.blogcntt.repository.QuestionRepository;
import com.example.blogcntt.repository.UserRepository;
import com.example.blogcntt.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Question>> getAllApprovedQuestions() {
        return ResponseEntity.ok(questionRepository.findByIsApprovedTrueOrderByCreatedAtDesc());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Question>> getAllPendingQuestions() {
        return ResponseEntity.ok(questionRepository.findByIsApprovedFalseOrderByCreatedAtDesc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        Optional<Question> question = questionRepository.findById(id);
        return question.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createQuestion(@Valid @RequestBody QuestionRequest questionRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId()).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found!"));
        }

        Question question = new Question();
        question.setTitle(questionRequest.getTitle());
        question.setContent(questionRequest.getContent());
        question.setUser(user);
        
        // Admins default to approved, users default to unapproved
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        question.setIsApproved(isAdmin);
        question.setStatus(isAdmin ? "APPROVED" : "PENDING");
        question.setCreatedAt(LocalDateTime.now());

        questionRepository.save(question);
        return ResponseEntity.ok(new MessageResponse(isAdmin ? "Question created successfully!" : "Question submitted for approval!"));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveQuestion(@PathVariable Long id) {
        Optional<Question> questionOptional = questionRepository.findById(id);
        if (questionOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Question question = questionOptional.get();
        question.setIsApproved(true);
        question.setStatus("APPROVED");
        questionRepository.save(question);

        return ResponseEntity.ok(new MessageResponse("Question approved successfully!"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        if (!questionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        questionRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Question deleted successfully!"));
    }
}
