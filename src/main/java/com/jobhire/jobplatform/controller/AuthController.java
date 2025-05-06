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
    @PutMapping("/profile/{id}")
public ResponseEntity<?> updateProfile(
        @PathVariable Long id,
        @RequestParam("name") String name,
        @RequestParam("email") String email,
        @RequestParam("bio") String bio,
        @RequestParam(value = "resume", required = false) MultipartFile resume) {

    System.out.println(">>> Updating profile for ID: " + id);
    System.out.println(">>> Name: " + name);
    System.out.println(">>> Email: " + email);
    System.out.println(">>> Bio: " + bio);
    System.out.println(">>> Resume is null? " + (resume == null));
    if (resume != null) {
        System.out.println(">>> Resume original name: " + resume.getOriginalFilename());
    }

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
            // 1. Define uploads directory outside of Tomcat temp
                String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
                File uploadFolder = new File(uploadDir);
                if (!uploadFolder.exists()) {
                    uploadFolder.mkdirs(); // Ensure it exists
                }

                // 2. Save file to the correct location
                String filePath = uploadDir + File.separator + "resume_" + id + "_" + resume.getOriginalFilename();
                File dest = new File(filePath);
                resume.transferTo(dest);

                // 3. Save accessible relative path to user entity
                user.setResumeUrl("/uploads/" + dest.getName());


            user.setResumeUrl("/uploads/" + fileName);
        } catch (IOException e) {
            e.printStackTrace(); // log actual error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save resume.");
        }
    }

    userRepo.save(user);
    return ResponseEntity.ok(user);
    }
}
