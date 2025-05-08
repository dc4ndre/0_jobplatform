package com.jobhire.jobplatform.controller;

import java.io.File;
import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jobhire.jobplatform.model.User;
import com.jobhire.jobplatform.repository.UserRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    // ✅ GET USER PROFILE BY ID
    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        Optional<User> userOpt = userRepo.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok(userOpt.get());
    }

    // ✅ REGISTER NEW USER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (user.getBio() == null || user.getBio().isEmpty()) {
            user.setBio("No bio provided.");
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("applicant");
        }

        userRepo.save(user);
        System.out.println("REGISTERED: " + user.getEmail());
        return ResponseEntity.ok(user);
    }

    // ✅ LOGIN CHECK
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User request) {
        User user = userRepo.findByEmailAndPassword(request.getEmail(), request.getPassword());
        if (user == null) {
            System.out.println("LOGIN FAILED: " + request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        System.out.println("LOGIN SUCCESS: " + request.getEmail());
        return ResponseEntity.ok(user);
    }

    // ✅ UPDATE PROFILE (WITH FILE UPLOAD)
    @PutMapping(path = "/profile/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("bio") String bio,
            @RequestParam(value = "resume", required = false) MultipartFile resume) {

        System.out.println(">>> Updating profile for ID: " + id);
        Optional<User> optionalUser = userRepo.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User ID not found.");
        }

        User user = optionalUser.get();
        user.setName(name);
        user.setEmail(email);
        user.setBio(bio);

        if (resume != null && !resume.isEmpty()) {
            try {
                String fileName = "resume_" + id + "_" + resume.getOriginalFilename();
                String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
                File uploadFolder = new File(uploadDir);
                if (!uploadFolder.exists()) uploadFolder.mkdirs();

                File dest = new File(uploadDir + File.separator + fileName);
                resume.transferTo(dest);
                user.setResumeUrl("/uploads/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save resume.");
            }
        }

        userRepo.save(user);
        return ResponseEntity.ok(user);
    }

    // ✅ JSON version for recruiter updates (no resume)
    @PutMapping(path = "/profile/{id}", consumes = "application/json")
    public ResponseEntity<?> updateProfileJson(
            @PathVariable Long id,
            @RequestBody User updatedUser) {

        Optional<User> optionalUser = userRepo.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User ID not found.");
        }

        User user = optionalUser.get();

        if (updatedUser.getName() != null) user.setName(updatedUser.getName());
        if (updatedUser.getEmail() != null) user.setEmail(updatedUser.getEmail());
        if (updatedUser.getBio() != null) user.setBio(updatedUser.getBio());

        userRepo.save(user);
        return ResponseEntity.ok(user);
    }
}
