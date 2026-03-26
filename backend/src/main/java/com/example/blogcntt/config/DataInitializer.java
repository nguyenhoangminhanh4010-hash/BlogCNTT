package com.example.blogcntt.config;

import com.example.blogcntt.entity.User;
import com.example.blogcntt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("123"); // NoOpPasswordEncoder is used in WebSecurityConfig
            admin.setRole("ADMIN");
            userRepository.save(admin);
            System.out.println("Admin user created successfully!");
        } else {
            System.out.println("Admin user already exists.");
        }
    }
}
