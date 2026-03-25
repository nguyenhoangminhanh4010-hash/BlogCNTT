package com.example.blogcntt.controller;

import com.example.blogcntt.entity.User;
import com.example.blogcntt.payload.request.LoginRequest;
import com.example.blogcntt.payload.request.SignupRequest;
import com.example.blogcntt.payload.response.JwtResponse;
import com.example.blogcntt.payload.response.MessageResponse;
import com.example.blogcntt.repository.UserRepository;
import com.example.blogcntt.security.JwtUtils;
import com.example.blogcntt.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
        String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        return ResponseEntity.ok(new JwtResponse(jwt, 
                                                 userDetails.getId(), 
                                                 userDetails.getUsername(), 
                                                 role));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(signUpRequest.getPassword());
        user.setRole("USER"); // Default role

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
