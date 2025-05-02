package com.jobhire.jobplatform.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/profile/{id}")
public ResponseEntity<?> getProfile(@PathVariable Long id) {
    Optional<User> userOpt = userRepo.findById(id);
    if (userOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }
    return ResponseEntity.ok(userOpt.get());
}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // ✅ Ensure bio is always set (avoid null values)
        if (user.getBio() == null || user.getBio().isEmpty()) {
            user.setBio("No bio provided.");
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("applicant"); // ✅ Default role if missing
        }

        userRepo.save(user);
        System.out.println("REGISTERED: " + user.getEmail() + " | Bio: " + user.getBio() + " | Role: " + user.getRole());
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
        return ResponseEntity.ok(user);  // ✅ Returns full user object
    }

    @PutMapping("/profile/{id}")
public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody User updatedUser) {
    Optional<User> optionalUser = userRepo.findById(id);
    if (optionalUser.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User ID not found.");
    }

    User existingUser = optionalUser.get();
    if (updatedUser.getName() != null) existingUser.setName(updatedUser.getName());
    if (updatedUser.getEmail() != null) existingUser.setEmail(updatedUser.getEmail());
    if (updatedUser.getBio() != null) existingUser.setBio(updatedUser.getBio());

    userRepo.save(existingUser);
    return ResponseEntity.ok(existingUser);
}

}