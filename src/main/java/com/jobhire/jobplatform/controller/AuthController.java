package com.jobhire.jobplatform.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobhire.jobplatform.model.User;
import com.jobhire.jobplatform.repository.UserRepository;


@CrossOrigin // allow frontend JS to call it if needed
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        userRepo.save(user);
        System.out.println("REGISTERED: " + user.getEmail());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody User request) {
            User user = userRepo.findByEmailAndPassword(request.getEmail(), request.getPassword());
            if (user == null) {
                System.out.println("LOGIN FAILED: " + request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
            System.out.println("LOGIN SUCCESS: " + request.getEmail());
            return ResponseEntity.ok(user);  // ✅ This ensures the user object is returned
        }

        @PutMapping("/profile/{id}")
            public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody User updatedUser) {
                Optional<User> optionalUser = userRepo.findById(id);
                if (optionalUser.isEmpty()) {
                    return ResponseEntity.notFound().build();
                }

                User existingUser = optionalUser.get();
                existingUser.setName(updatedUser.getName());
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setBio(updatedUser.getBio()); // ✅ set bio

                userRepo.save(existingUser);
                return ResponseEntity.ok(existingUser);
            }


    
    }
