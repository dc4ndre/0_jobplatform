package com.jobhire.jobplatform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobhire.jobplatform.model.Application;
import com.jobhire.jobplatform.model.Job;
import com.jobhire.jobplatform.model.User;
import com.jobhire.jobplatform.repository.ApplicationRepository;
import com.jobhire.jobplatform.repository.JobRepository;
import com.jobhire.jobplatform.repository.UserRepository;
import com.jobhire.jobplatform.service.EmailService;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin
public class ApplicationController {

    @Autowired
    private ApplicationRepository appRepo;

    @Autowired
    private JobRepository jobRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EmailService emailService;

    @PostMapping("/apply")
    public String apply(@RequestParam Long jobId, @RequestParam Long userId) {
        User user = userRepo.findById(userId).orElse(null);
        Job job = jobRepo.findById(jobId).orElse(null);

        if (user == null || job == null || !"applicant".equals(user.getRole())) {
            return "Invalid job or user";
        }

        Application app = new Application();
        app.setApplicant(user);
        app.setJob(job);
        app.setStatus("pending");
        appRepo.save(app);
        return "Application submitted";
    }

    @GetMapping("/job/{jobId}")
    public List<Application> getApplicants(@PathVariable Long jobId) {
        return appRepo.findAll().stream().filter(a -> a.getJob().getId().equals(jobId)).toList();
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Long appId, @RequestParam String status) {
        Application app = appRepo.findById(appId).orElse(null);
        if (app == null) return "Application not found";

        app.setStatus(status);
        appRepo.save(app);

        String email = app.getApplicant().getEmail();
        emailService.send(email, "Job Application Update", "Your application was " + status);
        return "Status updated + email sent";
    }
}
