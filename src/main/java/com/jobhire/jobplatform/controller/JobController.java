package com.jobhire.jobplatform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobhire.jobplatform.model.Job;
import com.jobhire.jobplatform.model.User;
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

    @PostMapping("/post")
    public String postJob(@RequestBody Job job, @RequestParam Long recruiterId) {
        User recruiter = userRepo.findById(recruiterId).orElse(null);
        if (recruiter == null || !"recruiter".equals(recruiter.getRole())) {
            return "Invalid recruiter ID";
        }
        job.setPostedBy(recruiter);
        jobRepo.save(job);
        return "Job posted successfully";
    }

    @GetMapping("/all")
    public List<Job> getAllJobs() {
        return jobRepo.findAll();
    }
}
