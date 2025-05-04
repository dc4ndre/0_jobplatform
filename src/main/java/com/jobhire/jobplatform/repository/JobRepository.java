package com.jobhire.jobplatform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobhire.jobplatform.model.Job;
import com.jobhire.jobplatform.model.JobStatus;
import com.jobhire.jobplatform.model.User;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByPostedById(Long recruiterId);
    List<Job> findByPostedBy(User recruiter);
    List<Job> findByStatusIn(List<JobStatus> statuses);  // used in getAllJobs
}
