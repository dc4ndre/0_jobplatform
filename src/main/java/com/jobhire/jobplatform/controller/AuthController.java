package com.jobhire.jobplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobhire.jobplatform.model.User;
import com.jobhire.jobplatform.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin

public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (userRepo.findByEmail(user.getEmail()) != null) {
            return "User already exists";
        }
        userRepo.save(user);
        return "Registered successfully";
    }

    @PostMapping("/login")
    public String login(@RequestBody User login) {
        User user = userRepo.findByEmail(login.getEmail());
        if (user != null && user.getPassword().equals(login.getPassword())) {
            return "Login successful as " + user.getRole();
        }
        return "Invalid credentials";
    }
}
