package com.jobhire.jobplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobhire.jobplatform.model.Profile;
import com.jobhire.jobplatform.model.User;
import com.jobhire.jobplatform.repository.ProfileRepository;
import com.jobhire.jobplatform.repository.UserRepository;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin
public class ProfileController {

    @Autowired
    private ProfileRepository profileRepo;

    @Autowired
    private UserRepository userRepo;

    // Create Profile
    @PostMapping("/create")
    public ResponseEntity<?> createProfile(@RequestBody Profile profile) {
        System.out.println("Received profile data: " + profile.getName() + ", " + profile.getEmail() + ", " + profile.getBio());
    
        User user = userRepo.findById(profile.getUser().getId()).orElse(null);
        if (user == null) {
            System.out.println("Error: User not found!");
            return ResponseEntity.badRequest().body("User not found");
        }
    
        profile.setUser(user);
        profileRepo.save(profile);
        System.out.println("Profile saved successfully!");
        return ResponseEntity.ok(profile);
    }

    // Get Profile
    @PutMapping("/update/{userId}")
public ResponseEntity<?> updateProfile(@PathVariable Long userId, @RequestBody Profile profileData) {
    User user = userRepo.findById(userId).orElse(null);
    if (user == null) {
        return ResponseEntity.badRequest().body("Error: User ID " + userId + " not found!");
    }

    // ✅ Ensure profile exists for this user before updating
    Profile profile = profileRepo.findByUser(user);
    if (profile == null) {
        return ResponseEntity.badRequest().body("Profile not found for user ID " + userId);
    }

    // ✅ Update profile details
    profile.setName(profileData.getName());
    profile.setEmail(profileData.getEmail());
    profile.setBio(profileData.getBio());

    profileRepo.save(profile); // ✅ Save updates to database
    System.out.println("✅ Profile updated successfully!");
    
    return ResponseEntity.ok(profile);
}
   
}
// ✅ This code is a Spring Boot controller for managing user profiles in a job platform application. It includes methods to create, retrieve, and update user profiles. The controller interacts with the ProfileRepository and UserRepository to perform CRUD operations on the Profile and User entities. The code also includes error handling for cases where the user or profile is not found.
// The use of @CrossOrigin allows for cross-origin requests, which is useful for frontend-backend communication. The controller methods return appropriate HTTP responses based on the success or failure of the operations.