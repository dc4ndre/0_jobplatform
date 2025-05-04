package com.jobhire.jobplatform.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.jobhire.jobplatform.model.JobStatus;
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
        job.setStatus(JobStatus.OPEN);
        jobRepo.save(job);
        
        jobRepo.flush(); // Force database commit
        return ResponseEntity.ok("Job posted successfully.");
    }

    @GetMapping
    public List<Job> getAllJobs() {
        return jobRepo.findByStatusIn(List.of(JobStatus.OPEN, JobStatus.FILLED));
    }

    @GetMapping("/{id}")
    public Job getJobById(@PathVariable Long id) {
        return jobRepo.findById(id).orElse(null);
    }

    @PutMapping("/{id}/mark-filled")
    public ResponseEntity<?> markJobAsFilled(@PathVariable Long id) {
        Optional<Job> jobOpt = jobRepo.findById(id);
        if (jobOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found.");
        }

        Job job = jobOpt.get();
        job.setStatus(JobStatus.FILLED);
        
        jobRepo.save(job);
        jobRepo.flush(); // Force database commit   
        return ResponseEntity.ok("Job marked as FILLED.");
    }

    @GetMapping("/recruiter/{recruiterId}")
    public List<Job> getJobsByRecruiter(@PathVariable Long recruiterId) {
        Optional<User> recruiter = userRepo.findById(recruiterId);
        if (recruiter.isEmpty()) {
            return List.of();
        }
        return jobRepo.findByPostedBy(recruiter.get());
    }

    @PutMapping("/{id}")
public ResponseEntity<?> updateJob(@PathVariable Long id, @RequestBody Map<String, String> body) {
    Optional<Job> existing = jobRepo.findById(id);
    if (existing.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found.");
    }

    Job job = existing.get();

    // Validate and update all fields
    if (body.containsKey("title")) job.setTitle(body.get("title"));
    if (body.containsKey("description")) job.setDescription(body.get("description"));
    if (body.containsKey("location")) job.setLocation(body.get("location"));
    if (body.containsKey("requirements")) job.setRequirements(body.get("requirements"));

    // Ensure status update works
    if (body.containsKey("status")) {
        try {
            job.setStatus(JobStatus.valueOf(body.get("status").toUpperCase()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Invalid job status. Allowed: OPEN, FILLED, CLOSED.");
        }
    }

  
    jobRepo.save(job);
    jobRepo.flush(); // Force database commit
    return ResponseEntity.ok("Job updated successfully.");
}

    // 🔄 SOFT DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDeleteJob(@PathVariable Long id) {
        Optional<Job> existing = jobRepo.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found.");
        }

        Job job = existing.get();
        job.setStatus(JobStatus.DELETED);
        jobRepo.save(job);
        return ResponseEntity.ok("Job marked as DELETED.");
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
    @PutMapping("/{id}/job-status")
public ResponseEntity<?> updateJobStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
    Optional<Job> jobOpt = jobRepo.findById(id);
    if (jobOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found.");

    Job job = jobOpt.get();

    // Update all fields
    if (body.containsKey("title")) job.setTitle(body.get("title"));
    if (body.containsKey("description")) job.setDescription(body.get("description"));
    if (body.containsKey("location")) job.setLocation(body.get("location"));
    if (body.containsKey("requirements")) job.setRequirements(body.get("requirements"));

    // Update job status safely
    if (body.containsKey("status")) {
        try {
            job.setStatus(JobStatus.valueOf(body.get("status").toUpperCase()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Invalid job status. Allowed: OPEN, FILLED, CLOSED.");
        }
    }

    jobRepo.save(job);
    jobRepo.flush(); // Ensure database commit

    return ResponseEntity.ok("Job updated successfully.");
}

}
