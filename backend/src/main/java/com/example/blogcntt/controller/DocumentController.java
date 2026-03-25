package com.example.blogcntt.controller;

import com.example.blogcntt.entity.Document;
import com.example.blogcntt.payload.request.DocumentRequest;
import com.example.blogcntt.payload.response.MessageResponse;
import com.example.blogcntt.repository.DocumentRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(documentRepository.findAllByOrderByCreatedAtDesc());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createDocument(@Valid @RequestBody DocumentRequest documentRequest) {
        Document document = new Document();
        document.setTitle(documentRequest.getTitle());
        document.setLink(documentRequest.getLink());
        document.setCreatedAt(LocalDateTime.now());

        documentRepository.save(document);
        return ResponseEntity.ok(new MessageResponse("Document added successfully!"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        if (!documentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        documentRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Document deleted successfully!"));
    }
}
