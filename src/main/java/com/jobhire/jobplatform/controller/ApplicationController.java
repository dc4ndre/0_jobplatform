package com.jobhire.jobplatform.controller;

import java.time.LocalDateTime;
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
import com.jobhire.jobplatform.service.EmailService;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JobRepository jobRepo;

    @Autowired
    private ApplicationRepository appRepo;

    @PostMapping("/apply")
    public ResponseEntity<?> applyToJob(@RequestBody Map<String, Long> data) {
        Long applicantId = data.get("applicantId");
        Long jobId = data.get("jobId");
        Long recruiterId = data.get("recruiterId");

        if (applicantId == null || jobId == null || recruiterId == null) {
            return ResponseEntity.badRequest().body("Missing one of the IDs (applicantId, jobId, recruiterId)");
        }

        Optional<User> applicant = userRepo.findById(applicantId);
        Optional<User> recruiter = userRepo.findById(recruiterId);
        Optional<Job> job = jobRepo.findById(jobId);

        if (applicant.isEmpty() || recruiter.isEmpty() || job.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Job not found.");
        }

        Application app = new Application();
        app.setApplicant(applicant.get());
        app.setRecruiter(recruiter.get());
        app.setJob(job.get());
        app.setStatus("Pending");
        app.setAppliedAt(LocalDateTime.now());
        app.setMessage("");

        appRepo.save(app);
        return ResponseEntity.ok("Application submitted.");
    }

    @GetMapping
    public ResponseEntity<?> getApplicationsByApplicant(@RequestParam Long applicantId) {
        try {
            Optional<User> applicant = userRepo.findById(applicantId);
            if (applicant.isEmpty()) {
                return ResponseEntity.badRequest().body("Applicant not found.");
            }

            List<Application> applications = appRepo.findByApplicantId(applicantId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error fetching applications: " + e.getMessage());
        }
    }

    @GetMapping("/by-recruiter")
    public ResponseEntity<?> getApplicationsByRecruiter(@RequestParam Long recruiterId) {
        try {
            System.out.println("🔍 Fetching applications for recruiterId: " + recruiterId);

            Optional<User> recruiter = userRepo.findById(recruiterId);
            if (recruiter.isEmpty()) {
                System.err.println("❌ Recruiter not found with ID: " + recruiterId);
                return ResponseEntity.badRequest().body("Recruiter not found.");
            }

            List<Application> applications = appRepo.findByRecruiterId(recruiterId);
            System.out.println("✅ Applications fetched: " + applications.size());

            for (Application app : applications) {
                try {
                    User applicant = app.getApplicant();
                    if (applicant != null) {
                        applicant.getName();
                        applicant.getEmail();
                        applicant.getBio();
                    } else {
                        System.err.println("⚠️ Null applicant in application ID: " + app.getId());
                    }

                    Job job = app.getJob();
                    if (job != null) {
                        job.getTitle();
                    } else {
                        System.err.println("⚠️ Null job in application ID: " + app.getId());
                    }
                } catch (Exception nestedEx) {
                    System.err.println("🚨 Error accessing fields for application ID: " + app.getId());
                    nestedEx.printStackTrace();
                }
            }

            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            System.err.println("🔥 Server error in /by-recruiter endpoint:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error fetching applications: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
    System.out.println("🔧 Received PUT request to update status of application ID: " + id);

    try {
        Optional<Application> appOpt = appRepo.findById(id);
        if (appOpt.isEmpty()) {
            System.err.println("❌ Application not found with ID: " + id);
            return ResponseEntity.status(404).body("Application not found with ID: " + id);
        }

        String newStatus = body.get("status");
        if (newStatus == null || newStatus.isBlank()) {
            return ResponseEntity.badRequest().body("Status is required.");
        }

        if (!List.of("Accepted", "Rejected", "Pending").contains(newStatus)) {
            return ResponseEntity.badRequest().body("Invalid status value.");
        }

        Application app = appOpt.get();
        app.setStatus(newStatus);
        appRepo.save(app);

        if (app.getApplicant() != null && app.getApplicant().getEmail() != null) {
            String subject = "Update on Your Job Application";

            String content = String.format(
                "Hi %s,\n\n" +
                "We wanted to let you know that your application for the role of **%s** has been updated to: **%s**.\n\n" +
                "If your application was accepted, the recruiter will be in touch with you soon via email to provide further details and coordinate your interview schedule.\n\n" +
                "Thanks again for applying through JobiJobi! If you have any questions, feel free to reach out.\n\n" +
                "Warm regards,\n" +
                "The JobiJobi Team",
                app.getApplicant().getName(),
                app.getJob().getTitle(),
                newStatus
            );

            emailService.sendSimpleMessage(app.getApplicant().getEmail(), subject, content);
        }

        return ResponseEntity.ok(Map.of(
            "message", "Status updated successfully",
            "applicationId", id,
            "newStatus", newStatus
        ));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body("Error updating status: " + e.getMessage());
    }
}

}
