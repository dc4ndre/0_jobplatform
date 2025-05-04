package com.jobhire.jobplatform.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties({"applications", "jobs", "password"}) // ✅ keep email, bio, name visible
    private User applicant;

    @ManyToOne
    @JsonIgnoreProperties({"applications", "jobs", "password"}) // ✅ same here
    private User recruiter;

    @ManyToOne
    @JsonIgnoreProperties({"applications", "postedBy"}) // ✅ job title accessible
    private Job job;

    private LocalDateTime appliedAt;

    @Column(nullable = false)
    private String status = "Pending";

    @Column(length = 1000)
    private String message;

    // Getters & Setters
    public Long getId() { return id; }

    public User getApplicant() { return applicant; }
    public void setApplicant(User applicant) { this.applicant = applicant; }

    public User getRecruiter() { return recruiter; }
    public void setRecruiter(User recruiter) { this.recruiter = recruiter; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
