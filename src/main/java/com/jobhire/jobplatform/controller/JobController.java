package com.jobhire.jobplatform.controller;

import java.util.List;
import java.util.Map;
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

import com.jobhire.jobplatform.model.Application;
import com.jobhire.jobplatform.model.Job;
import com.jobhire.jobplatform.model.User;
import com.jobhire.jobplatform.repository.ApplicationRepository;
import com.jobhire.jobplatform.repository.JobRepository;
import com.jobhire.jobplatform.repository.UserRepository;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin
public class JobController {

    @Autowired
    private JobRepository jobRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ApplicationRepository appRepo;

    @PostMapping("/post")
    public ResponseEntity<?> postJob(@RequestBody Job job, @RequestParam Long recruiterId) {
        Optional<User> recruiter = userRepo.findById(recruiterId);
        if (recruiter.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recruiter not found.");
        }

        job.setPostedBy(recruiter.get());
        jobRepo.save(job);
        return ResponseEntity.ok("Job posted successfully.");
    }

    @GetMapping
    public List<Job> getAllJobs() {
        return jobRepo.findAll();
    }

    @GetMapping("/{id}")
    public Job getJobById(@PathVariable Long id) {
        return jobRepo.findById(id).orElse(null);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Optional<Application> appOpt = appRepo.findById(id);
        if (appOpt.isEmpty()) return ResponseEntity.notFound().build();

        String newStatus = body.get("status");
        if (newStatus == null || newStatus.isBlank()) {
            return ResponseEntity.badRequest().body("Status is required.");
        }

        Application app = appOpt.get();
        app.setStatus(newStatus);
        appRepo.save(app);

        return ResponseEntity.ok("Application status updated.");
    }
}
